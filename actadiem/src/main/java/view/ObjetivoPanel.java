package view;

import controller.ObjetivoController;
import dao.ObjetivoDAO;
import model.Objetivo;
import model.StatusObjetivo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ObjetivoPanel extends JPanel {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JComboBox<StatusObjetivo> statusCombo;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Objetivo> objetivosList;
    private DefaultListModel<Objetivo> listModel;
    private ObjetivoController objetivoController;
    private Long editandoId;

    public ObjetivoPanel() {
        objetivoController = new ObjetivoController(new ObjetivoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelTitulo(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelListagem(), BorderLayout.CENTER);

        carregarObjetivos();
    }

    private JPanel criarPainelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Gerenciar Objetivos");
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
        statusCombo = new JComboBox<>(StatusObjetivo.values());
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
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(statusCombo, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(salvarButton);
        panel.add(botoesPanel, gbc);

        salvarButton.addActionListener(e -> salvarObjetivo());
        cancelarButton.addActionListener(e -> cancelarEdicao());

        return panel;
    }

    private JPanel criarPainelListagem() {
        listModel = new DefaultListModel<>();
        objetivosList = new JList<>(listModel);
        objetivosList.setCellRenderer(new ObjetivoListRenderer());

        JButton editarButton = new JButton("Editar");
        JButton excluirButton = new JButton("Excluir");

        editarButton.addActionListener(e -> editarObjetivo());
        excluirButton.addActionListener(e -> excluirObjetivo());

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        botoesPanel.add(editarButton);
        botoesPanel.add(excluirButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Meus objetivos"));
        panel.add(new JScrollPane(objetivosList), BorderLayout.CENTER);
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void carregarObjetivos() {
        listModel.clear();
        List<Objetivo> objetivos = objetivoController.listarTodos();
        for (Objetivo objetivo : objetivos) {
            listModel.addElement(objetivo);
        }
    }

    private void editarObjetivo() {
        Objetivo selecionado = objetivosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um objetivo para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editandoId = selecionado.getId();
        tituloField.setText(selecionado.getTitulo());
        descricaoField.setText(selecionado.getDescricao());
        statusCombo.setSelectedItem(selecionado.getStatus());
        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
    }

    private void excluirObjetivo() {
        Objetivo selecionado = objetivosList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um objetivo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Excluir o objetivo \"" + selecionado.getTitulo() + "\"?",
                "Confirmar exclus\u00e3o",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            objetivoController.deletar(selecionado.getId());
            carregarObjetivos();
        }
    }

    private void cancelarEdicao() {
        editandoId = null;
        tituloField.setText("");
        descricaoField.setText("");
        statusCombo.setSelectedIndex(0);
        salvarButton.setText("Salvar");
        cancelarButton.setVisible(false);
    }

    private void salvarObjetivo() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O t\u00edtulo \u00e9 obrigat\u00f3rio!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Objetivo objetivo = new Objetivo();
        objetivo.setTitulo(titulo);
        objetivo.setDescricao(descricao);
        objetivo.setStatus((StatusObjetivo) statusCombo.getSelectedItem());

        if (editandoId != null) {
            objetivo.setId(editandoId);
            objetivoController.atualizar(objetivo);
            cancelarEdicao();
        } else {
            objetivoController.salvar(objetivo);
            tituloField.setText("");
            descricaoField.setText("");
            statusCombo.setSelectedIndex(0);
        }

        carregarObjetivos();
    }

    private static class ObjetivoListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Objetivo objetivo) {
                String statusLabel = switch (objetivo.getStatus()) {
                    case EM_PLANEJAMENTO -> "Em planejamento";
                    case PLANEJADO -> "Planejado";
                    case EM_EXECUCAO -> "Em execu\u00e7\u00e3o";
                    case ALCANCADO -> "Alcan\u00e7ado";
                    case NAO_ALCANCADO -> "N\u00e3o alcan\u00e7ado";
                };
                setText(objetivo.getTitulo() + " - " + statusLabel);
            }
            return c;
        }
    }
}
