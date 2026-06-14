package controller;

import dao.UsuarioDAO;
import model.Usuario;

public class UsuarioController {
    private static UsuarioController instance;
    private static Usuario usuarioAtivo;
    private static UsuarioDAO usuarioDAO;

    private UsuarioController() {
        usuarioDAO = new UsuarioDAO();
    }

    public static UsuarioController getInstance() {
        if (instance == null) {
           instance = new UsuarioController();
        }
        return instance;
    }

    public boolean estaLogado() {
        return usuarioAtivo != null;
    }

    public void logar(String email) {
        usuarioAtivo = usuarioDAO.encontrarPorEmail(email);
        if (usuarioAtivo == null) {
            throw new IllegalArgumentException("Usuario com email " + email + " nao encontrado!");
        }
    }

    public static Usuario getUsuarioAtivo() {
        return usuarioAtivo;
    }
}
