package view;

import controller.UsuarioController;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField emailField;
    private JButton loginButton;
    private UsuarioController usuarioController;
    private Runnable onLoginSuccess;

    public LoginView(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        usuarioController = UsuarioController.getInstance();
        setTitle("Actadiem - Login");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        emailField = new JTextField(20);
        loginButton = new JButton("Entrar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(emailField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> logar());

        setVisible(true);
    }

    private void logar() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "O email \u00e9 obrigat\u00f3rio!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        usuarioController.logar(email);

        JOptionPane.showMessageDialog(
                this,
                "Login realizado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        onLoginSuccess.run();
    }
}
