package dao;

import model.Evento;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.SQLException;

public class EventoDAO {
    private Connection conn;

    public EventoDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Evento evento, Usuario usuario) {
        String sql = "INSERT INTO evento (titulo, descricao, usuario_id) VALUES (?, ?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, evento.getTitulo().trim());
            ps.setString(2, evento.getDescricao().trim());
            ps.setLong(3, usuario.getId());

            return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o evento.",e);
        }
    }
}
