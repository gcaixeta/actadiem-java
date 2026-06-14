package view;

import controller.EventoController;
import dao.EventoDAO;
import model.Evento;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventoPanel extends JPanel {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JButton salvarButton;
    private JButton cancelarButton;
    private JList<Evento> eventosList;
    private DefaultListModel<Evento> listModel;
    private EventoController eventoController;
    private Long editandoId;

    public EventoPanel() {
        eventoController = new EventoController(new EventoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelSaudacao(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelResumo(), BorderLayout.CENTER);

        carregarEventosHoje();
    }

    private JPanel criarPainelSaudacao() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("O que est\u00e1 prestes a acontecer?");
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
        salvarButton = new JButton("Registrar");
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

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 0;
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
        salvarButton.setText("Atualizar");
        cancelarButton.setVisible(true);
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
                "Confirmar exclus\u00e3o",
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
        salvarButton.setText("Registrar");
        cancelarButton.setVisible(false);
    }

    private void salvarEvento() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O t\u00edtulo \u00e9 obrigat\u00f3rio!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (editandoId != null) {
            Evento evento = new Evento(titulo, descricao);
            evento.setId(editandoId);
            eventoController.atualizar(evento);
            cancelarEdicao();
        } else {
            eventoController.registrarEvento(new Evento(titulo, descricao));
            tituloField.setText("");
            descricaoField.setText("");
        }

        carregarEventosHoje();
    }

    private static class EventoListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Evento evento) {
                setText(evento.getTitulo() + " - " + evento.getDescricao());
            }
            return c;
        }
    }
}
