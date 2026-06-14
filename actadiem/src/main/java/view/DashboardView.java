package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardView() {
        setTitle("Actadiem");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(criarSidebar(), BorderLayout.WEST);
        add(criarContentArea(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(45, 45, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        Usuario usuario = UsuarioController.getUsuarioAtivo();

        JLabel userLabel = new JLabel(usuario != null ? usuario.getNome() : "Usu\u00e1rio");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        sidebar.add(userLabel, gbc);

        gbc.gridy = 1;
        sidebar.add(new JSeparator(), gbc);

        String[][] items = {
            {"Eventos", "eventos"},
            {"H\u00e1bitos", "habitos"},
            {"Objetivos", "objetivos"},
            {"Tarefas", "tarefas"},
        };

        for (int i = 0; i < items.length; i++) {
            JButton btn = new JButton(items[i][0]);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(60, 60, 60));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String cardName = items[i][1];
            btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));

            gbc.gridy = i + 2;
            sidebar.add(btn, gbc);
        }

        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(180, 40, 40));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        gbc.gridy = items.length + 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        sidebar.add(logoutBtn, gbc);

        return sidebar;
    }

    private JPanel criarContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new EventoPanel(), "eventos");
        contentPanel.add(new HabitoPanel(), "habitos");
        contentPanel.add(new ObjetivoPanel(), "objetivos");
        contentPanel.add(new TarefaPanel(), "tarefas");

        return contentPanel;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair?",
                "Sair",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginView(() -> SwingUtilities.invokeLater(DashboardView::new)));
        }
    }
}
