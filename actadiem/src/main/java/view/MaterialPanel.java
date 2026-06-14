package view;

import controller.MaterialController;
import controller.ObjetivoController;
import controller.TarefaController;
import dao.MaterialDAO;
import dao.ObjetivoDAO;
import dao.TarefaDAO;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialPanel extends JPanel {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JComboBox<TipoMaterial> tipoCombo;
    private JTextField urlField;
    private JList<Tarefa> tarefasList;
    private JList<Objetivo> objetivosList;
    private DefaultListModel<Tarefa> tarefasListModel;
    private DefaultListModel<Objetivo> objetivosListModel;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Material> materiaisList;
    private DefaultListModel<Material> listModel;
    private MaterialController materialController;
    private TarefaController tarefaController;
    private ObjetivoController objetivoController;
    private Long editandoId;

    public MaterialPanel() {
        materialController = new MaterialController(new MaterialDAO());
        tarefaController = new TarefaController(new TarefaDAO());
        objetivoController = new ObjetivoController(new ObjetivoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelListagem(), BorderLayout.CENTER);

        carregarTarefas();
        carregarObjetivos();
        carregarMateriais();
    }

    private JPanel criarPainelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Gerenciar Materiais");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        tituloField = new JTextField(20);
        descricaoField = new JTextArea(3, 20);
        tipoCombo = new JComboBox<>(TipoMaterial.values());
        urlField = new JTextField(20);
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("T\u00edtulo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tituloField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(new JLabel("Descri\u00e7\u00e3o:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descricaoField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tipoCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("URL:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(urlField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Tarefas:"), gbc);

        tarefasListModel = new DefaultListModel<>();
        tarefasList = new JList<>(tarefasListModel);
        tarefasList.setCellRenderer(new TarefaLinkRenderer());
        tarefasList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tarefasList.setVisibleRowCount(3);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        panel.add(new JScrollPane(tarefasList), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Objetivos:"), gbc);

        objetivosListModel = new DefaultListModel<>();
        objetivosList = new JList<>(objetivosListModel);
        objetivosList.setCellRenderer(new ObjetivoLinkRenderer());
        objetivosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        objetivosList.setVisibleRowCount(3);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        panel.add(new JScrollPane(objetivosList), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        panel.add(botoesPanel, gbc);

        salvarButton.addActionListener(e -> salvarMaterial());
        cancelarButton.addActionListener(e -> cancelarEdicao());

        return panel;
    }

    private JPanel criarPainelListagem() {
        listModel = new DefaultListModel<>();
        materiaisList = new JList<>(listModel);
        materiaisList.setCellRenderer(new MaterialListRenderer());

        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        editarButton.addActionListener(e -> editarMaterial());
        excluirButton.addActionListener(e -> excluirMaterial());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        botoesPanel.add(editarButton);
        botoesPanel.add(excluirButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Meus materiais"));
        panel.add(new JScrollPane(materiaisList), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarTarefas() {
        tarefasListModel.clear();
        List<Tarefa> tarefas = tarefaController.listarTodos();
        for (Tarefa tarefa : tarefas) {
            tarefasListModel.addElement(tarefa);
        }
    }

    private void carregarObjetivos() {
        objetivosListModel.clear();
        List<Objetivo> objetivos = objetivoController.listarTodos();
        for (Objetivo objetivo : objetivos) {
            objetivosListModel.addElement(objetivo);
        }
    }

    private void carregarMateriais() {
        listModel.clear();
        List<Material> materiais = materialController.listarTodos();
        for (Material material : materiais) {
            listModel.addElement(material);
        }
    }

    private void editarMaterial() {
        Material selecionado = materiaisList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um material para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editandoId = selecionado.getId();
        tituloField.setText(selecionado.getTitulo());
        descricaoField.setText(selecionado.getDescricao());
        tipoCombo.setSelectedItem(selecionado.getTipo());
        urlField.setText(selecionado.getUrl());

        selecionarItens(tarefasList, selecionado.getTarefas());
        selecionarItens(objetivosList, selecionado.getObjetivos());

        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
    }

    private void selecionarItens(JList<?> list, List<?> selecionados) {
        ListModel<?> model = list.getModel();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            Object item = model.getElementAt(i);
            for (Object sel : selecionados) {
                if (item instanceof Tarefa t1 && sel instanceof Tarefa t2 && t1.getId().equals(t2.getId())) {
                    indices.add(i);
                    break;
                }
                if (item instanceof Objetivo o1 && sel instanceof Objetivo o2 && o1.getId().equals(o2.getId())) {
                    indices.add(i);
                    break;
                }
            }
        }
        int[] idxArray = indices.stream().mapToInt(Integer::intValue).toArray();
        list.setSelectedIndices(idxArray);
    }

    private void excluirMaterial() {
        Material selecionado = materiaisList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um material para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Excluir o material \"" + selecionado.getTitulo() + "\"?",
                "Confirmar exclus\u00e3o",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            materialController.deletar(selecionado.getId());
            carregarMateriais();
        }
    }

    private void cancelarEdicao() {
        editandoId = null;
        tituloField.setText("");
        descricaoField.setText("");
        tipoCombo.setSelectedIndex(0);
        urlField.setText("");
        tarefasList.clearSelection();
        objetivosList.clearSelection();
        salvarButton.setText("Salvar");
        cancelarButton.setVisible(false);
    }

    private void salvarMaterial() {
        String titulo = tituloField.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O t\u00edtulo \u00e9 obrigat\u00f3rio!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Material material = new Material();
        material.setTitulo(titulo);
        material.setDescricao(descricaoField.getText().trim());
        material.setTipo((TipoMaterial) tipoCombo.getSelectedItem());
        material.setUrl(urlField.getText().trim());

        material.setTarefas(tarefasList.getSelectedValuesList());
        material.setObjetivos(objetivosList.getSelectedValuesList());

        if (editandoId != null) {
            material.setId(editandoId);
            materialController.atualizar(material);
            cancelarEdicao();
        } else {
            materialController.salvar(material);
            tituloField.setText("");
            descricaoField.setText("");
            tipoCombo.setSelectedIndex(0);
            urlField.setText("");
            tarefasList.clearSelection();
            objetivosList.clearSelection();
        }

        carregarMateriais();
    }

    private static class MaterialListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Material material) {
                String tipo = switch (material.getTipo()) {
                    case VIDEO -> "V\u00eddeo";
                    case LIVRO -> "Livro";
                    case EBOOK -> "E-book";
                    case ARTIGO -> "Artigo";
                    case PODCAST -> "Podcast";
                    case DOCUMENTACAO -> "Documenta\u00e7\u00e3o";
                    case OUTRO -> "Outro";
                };

                StringBuilder sb = new StringBuilder();
                sb.append("[").append(tipo).append("] ").append(material.getTitulo());

                if (!material.getTarefas().isEmpty()) {
                    sb.append(" | Tarefas: ");
                    for (int i = 0; i < material.getTarefas().size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(material.getTarefas().get(i).getTitulo());
                    }
                }
                if (!material.getObjetivos().isEmpty()) {
                    sb.append(" | Objetivos: ");
                    for (int i = 0; i < material.getObjetivos().size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(material.getObjetivos().get(i).getTitulo());
                    }
                }

                setText(sb.toString());
            }
            return c;
        }
    }

    private static class TarefaLinkRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tarefa tarefa) {
                String prioridade = switch (tarefa.getPrioridade()) {
                    case BAIXA -> "Baixa";
                    case MEDIA -> "M\u00e9dia";
                    case GRANDE -> "Grande";
                };
                setText(tarefa.getTitulo() + " (" + prioridade + ")");
            }
            return c;
        }
    }

    private static class ObjetivoLinkRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Objetivo objetivo) {
                String status = switch (objetivo.getStatus()) {
                    case EM_PLANEJAMENTO -> "Planejamento";
                    case PLANEJADO -> "Planejado";
                    case EM_EXECUCAO -> "Execu\u00e7\u00e3o";
                    case ALCANCADO -> "Alcan\u00e7ado";
                    case NAO_ALCANCADO -> "N\u00e3o alcan\u00e7ado";
                };
                setText(objetivo.getTitulo() + " (" + status + ")");
            }
            return c;
        }
    }
}
