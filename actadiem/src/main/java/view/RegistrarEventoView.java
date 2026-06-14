package view;

import controller.EventoController;
import dao.EventoDAO;
import model.Evento;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegistrarEventoView extends JFrame {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JButton registrarButton;
    private JList<Evento> eventosList;
    private DefaultListModel<Evento> listModel;
    private EventoController eventoController;

    public RegistrarEventoView() {
        eventoController = new EventoController(new EventoDAO());
        setTitle("Actadiem - Registrar Evento");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        add(criarPainelSaudacao(), BorderLayout.NORTH);
        add(criarPainelFormulario(), BorderLayout.CENTER);
        add(criarPainelResumo(), BorderLayout.SOUTH);

        carregarEventosHoje();

        setVisible(true);
    }

    private JPanel criarPainelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        tituloField = new JTextField(20);
        descricaoField = new JTextArea(5, 20);
        registrarButton = new JButton("Registrar");

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
        panel.add(registrarButton, gbc);

        registrarButton.addActionListener(e -> registrarEvento());

        return panel;
    }

    private JPanel criarPainelSaudacao() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("O que est\u00e1 prestes a acontecer?");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarPainelResumo() {
        listModel = new DefaultListModel<>();
        eventosList = new JList<>(listModel);
        eventosList.setCellRenderer(new EventoListRenderer());
        eventosList.setVisibleRowCount(4);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resumo do dia"));
        panel.add(new JScrollPane(eventosList), BorderLayout.CENTER);

        return panel;
    }

    private void carregarEventosHoje() {
        listModel.clear();
        List<Evento> eventos = eventoController.listarEventosHoje();
        for (Evento evento : eventos) {
            listModel.addElement(evento);
        }
    }

    private void registrarEvento() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O t\u00edtulo \u00e9 obrigat\u00f3rio!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        eventoController.registrarEvento(new Evento(titulo, descricao));

        JOptionPane.showMessageDialog(
                this,
                "Evento registrado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        tituloField.setText("");
        descricaoField.setText("");
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