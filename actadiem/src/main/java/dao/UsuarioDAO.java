package dao;

import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection conn;

    public UsuarioDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Usuario encontrarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por email.", e);
        }

        return null;
    }

}
