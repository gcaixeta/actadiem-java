import view.RegistrarEventoView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            RegistrarEventoView tela = new RegistrarEventoView();
            tela.setVisible(true);
        });
    }
}
