package view;

import controller.EventoController;
import dao.EventoDAO;
import model.Evento;

import javax.swing.*;
import java.awt.*;

public class RegistrarEventoView extends JFrame {
    private JTextField tituloField;
    private JTextArea descricaoField;
    private JButton registrarButton;
    private EventoController eventoController;

    public RegistrarEventoView() {
        eventoController = new EventoController(new EventoDAO());
        setTitle("Actadiem - Registrar Evento");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        tituloField = new JTextField(20);
        descricaoField = new JTextArea(5, 20);
        registrarButton = new JButton("Registrar");

        // Label título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Título:"), gbc);

        // Campo título
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tituloField, gbc);

        // Label descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(new JLabel("Descrição:"), gbc);

        // Campo descrição
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descricaoField), gbc);

        // Botão
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(registrarButton, gbc);

        add(panel);

        registrarButton.addActionListener(e -> registrarEvento());

        setVisible(true);
    }

    private void registrarEvento() {
        String titulo = tituloField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O titulo e obrigatorio!",
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
    }
}