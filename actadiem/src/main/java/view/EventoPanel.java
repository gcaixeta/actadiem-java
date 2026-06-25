package view;

import controller.EventoController;
import controller.ObjetivoController;
import dao.EventoDAO;
import dao.ObjetivoDAO;
import event.DataChangeBus;
import model.Evento;
import model.Objetivo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventoPanel extends JPanel {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JList<Objetivo> objetivosList;
    private DefaultListModel<Objetivo> objetivosListModel;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Evento> eventosList;
    private DefaultListModel<Evento> listModel;
    private EventoController eventoController;
    private ObjetivoController objetivoController;
    private Long editandoId;
    private final DataChangeBus.Listener objetivosListener = this::carregarObjetivos;

    public EventoPanel() {
        eventoController = new EventoController(new EventoDAO());
        objetivoController = new ObjetivoController(new ObjetivoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelSaudacao(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelResumo(), BorderLayout.CENTER);

        carregarObjetivos();
        carregarEventosHoje();
    }

    private JPanel criarPainelSaudacao() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("O que está prestes a acontecer?");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        tituloField = new JTextField(20);
        descricaoField = new JTextArea(5, 20);
        descricaoField.setLineWrap(true);
        descricaoField.setWrapStyleWord(true);
        salvarButton = new JButton("Registrar");
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Título:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tituloField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descricaoField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Objetivos:"), gbc);

        objetivosListModel = new DefaultListModel<>();
        objetivosList = new JList<>(objetivosListModel);
        objetivosList.setCellRenderer(new ObjetivoLinkRenderer());
        objetivosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        objetivosList.setVisibleRowCount(4);
        CheckboxLists.enable(objetivosList);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        panel.add(new JScrollPane(objetivosList), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        panel.add(botoesPanel, gbc);

        salvarButton.addActionListener(e -> salvarEvento());
        cancelarButton.addActionListener(e -> cancelarEdicao());

        return panel;
    }

    private JPanel criarPainelResumo() {
        listModel = new DefaultListModel<>();
        eventosList = new JList<>(listModel);
        eventosList.setCellRenderer(new EventoListRenderer());

        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        editarButton.addActionListener(e -> editarEvento());
        excluirButton.addActionListener(e -> excluirEvento());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        botoesPanel.add(editarButton);
        botoesPanel.add(excluirButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resumo do dia"));
        panel.add(new JScrollPane(eventosList), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        DataChangeBus.subscribe(DataChangeBus.Topic.OBJETIVOS, objetivosListener);
    }

    @Override
    public void removeNotify() {
        DataChangeBus.unsubscribe(DataChangeBus.Topic.OBJETIVOS, objetivosListener);
        super.removeNotify();
    }

    private void carregarObjetivos() {
        List<Objetivo> selecionados = objetivosList.getSelectedValuesList();
        objetivosListModel.clear();
        List<Objetivo> objetivos = objetivoController.listarTodos();
        for (Objetivo objetivo : objetivos) {
            objetivosListModel.addElement(objetivo);
        }
        selecionarObjetivos(selecionados);
    }

    private void carregarEventosHoje() {
        listModel.clear();
        List<Evento> eventos = eventoController.listarEventosHoje();
        for (Evento evento : eventos) {
            listModel.addElement(evento);
        }
    }

    private void editarEvento() {
        Evento selecionado = eventosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editandoId = selecionado.getId();
        tituloField.setText(selecionado.getTitulo());
        descricaoField.setText(selecionado.getDescricao());
        selecionarObjetivos(selecionado.getObjetivos());
        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
    }

    private void selecionarObjetivos(List<Objetivo> selecionados) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < objetivosListModel.getSize(); i++) {
            Objetivo item = objetivosListModel.getElementAt(i);
            for (Objetivo sel : selecionados) {
                if (item.getId().equals(sel.getId())) {
                    indices.add(i);
                    break;
                }
            }
        }
        objetivosList.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
    }

    private void excluirEvento() {
        Evento selecionado = eventosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Excluir o evento \"" + selecionado.getTitulo() + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            eventoController.deletar(selecionado.getId());
            carregarEventosHoje();
        }
    }

    private void cancelarEdicao() {
        editandoId = null;
        tituloField.setText("");
        descricaoField.setText("");
        objetivosList.clearSelection();
        salvarButton.setText("Registrar");
        cancelarButton.setVisible(false);
    }

    private void salvarEvento() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O título é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Evento evento = new Evento(titulo, descricao);
        evento.setObjetivos(objetivosList.getSelectedValuesList());

        if (editandoId != null) {
            evento.setId(editandoId);
            eventoController.atualizar(evento);
            cancelarEdicao();
        } else {
            eventoController.registrarEvento(evento);
            tituloField.setText("");
            descricaoField.setText("");
            objetivosList.clearSelection();
        }

        carregarEventosHoje();
    }

    private static class EventoListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Evento evento) {
                StringBuilder sb = new StringBuilder();
                sb.append(evento.getTitulo()).append(" - ").append(evento.getDescricao());

                if (!evento.getObjetivos().isEmpty()) {
                    sb.append(" | Objetivos: ");
                    for (int i = 0; i < evento.getObjetivos().size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(evento.getObjetivos().get(i).getTitulo());
                    }
                }

                setText(sb.toString());
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
                    case EM_EXECUCAO -> "Execução";
                    case ALCANCADO -> "Alcançado";
                    case NAO_ALCANCADO -> "Não alcançado";
                };
                setText(objetivo.getTitulo() + " (" + status + ")");
            }
            return c;
        }
    }
}
