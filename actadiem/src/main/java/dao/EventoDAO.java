package dao;

import model.Evento;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventoDAO {
    private Connection conn;

    public EventoDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Evento evento) {
        String sql = "INSERT INTO eventos (titulo, descricao) VALUES (?, ?);";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(0, evento.getTitulo());
            ps.setString(1, evento.getDescricao());

            return ps.executeLargeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o evento.",e);
        }
    }
}
