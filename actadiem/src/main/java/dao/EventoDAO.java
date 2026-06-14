package dao;

import model.Evento;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private Connection conn;

    public EventoDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Evento evento, Usuario usuario) {
        String sql = "INSERT INTO evento (titulo, descricao, usuario_id, data) VALUES (?, ?, ?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, evento.getTitulo().trim());
            ps.setString(2, evento.getDescricao().trim());
            ps.setLong(3, usuario.getId());

            LocalDate data = evento.getDate();
            if (data == null) {
                data = LocalDate.now();
            }
            ps.setDate(4, Date.valueOf(data));

            return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o evento.", e);
        }
    }

    public List<Evento> listarPorData(LocalDate data, Usuario usuario) {
        String sql = "SELECT id, titulo, descricao, data FROM evento WHERE data = ? AND usuario_id = ? ORDER BY id";
        List<Evento> eventos = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ps.setLong(2, usuario.getId());

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento(
                            rs.getString("titulo"),
                            rs.getString("descricao")
                    );
                    evento.setId(rs.getLong("id"));
                    evento.setData(rs.getDate("data").toLocalDate());
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos por data.", e);
        }

        return eventos;
    }

    public void atualizar(Evento evento) {
        String sql = "UPDATE evento SET titulo = ?, descricao = ?, data = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, evento.getTitulo().trim());
            ps.setString(2, evento.getDescricao().trim());

            LocalDate data = evento.getDate();
            if (data == null) {
                data = LocalDate.now();
            }
            ps.setDate(3, Date.valueOf(data));
            ps.setLong(4, evento.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel atualizar o evento.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar o evento.", e);
        }
    }
}
