import view.LoginView;
import view.RegistrarEventoView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginView(() -> SwingUtilities.invokeLater(RegistrarEventoView::new));
        });
    }
}
