import view.DashboardView;
import view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginView(() -> SwingUtilities.invokeLater(DashboardView::new));
        });
    }
}
