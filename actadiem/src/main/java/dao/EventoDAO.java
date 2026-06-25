package dao;

import model.Evento;
import model.Objetivo;
import model.StatusObjetivo;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
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
        try (var ps = conn.prepareStatement(sql, new String[]{"id"})) {
            ps.setString(1, evento.getTitulo().trim());
            ps.setString(2, evento.getDescricao().trim());
            ps.setLong(3, usuario.getId());

            LocalDate data = evento.getDate();
            if (data == null) {
                data = LocalDate.now();
            }
            ps.setDate(4, Date.valueOf(data));

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    salvarObjetivos(id, evento.getObjetivos());
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o evento.", e);
        }
        return null;
    }

    private void salvarObjetivos(Long eventoId, List<Objetivo> objetivos) {
        String sql = "INSERT INTO evento_objetivo (evento_id, objetivo_id) VALUES (?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            for (Objetivo objetivo : objetivos) {
                ps.setLong(1, eventoId);
                ps.setLong(2, objetivo.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar relacoes com objetivos.", e);
        }
    }

    public List<Evento> listarPorData(LocalDate data, Usuario usuario) {
        String sql = "SELECT id, titulo, descricao, nota, comentario, data FROM evento WHERE data = ? AND usuario_id = ? ORDER BY id";
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
                    int nota = rs.getInt("nota");
                    evento.setNota(rs.wasNull() ? null : nota);
                    evento.setComentario(rs.getString("comentario"));
                    evento.setData(rs.getDate("data").toLocalDate());
                    evento.setObjetivos(buscarObjetivosPorEvento(evento.getId()));
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos por data.", e);
        }

        return eventos;
    }

    public void avaliar(Long eventoId, Integer nota, String comentario) {
        String sql = "UPDATE evento SET nota = ?, comentario = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            if (nota == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, nota);
            }
            if (comentario == null || comentario.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, comentario.trim());
            }
            ps.setLong(3, eventoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar a avaliacao do evento.", e);
        }
    }

    private List<Objetivo> buscarObjetivosPorEvento(Long eventoId) {
        String sql = """
            SELECT o.id, o.titulo, o.descricao, o.status
            FROM objetivo o
            INNER JOIN evento_objetivo eo ON eo.objetivo_id = o.id
            WHERE eo.evento_id = ?
            ORDER BY o.id
        """;
        List<Objetivo> objetivos = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventoId);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Objetivo objetivo = new Objetivo();
                    objetivo.setId(rs.getLong("id"));
                    objetivo.setTitulo(rs.getString("titulo"));
                    objetivo.setDescricao(rs.getString("descricao"));
                    objetivo.setStatus(StatusObjetivo.fromValue(rs.getInt("status")));
                    objetivos.add(objetivo);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar objetivos do evento.", e);
        }

        return objetivos;
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

        deletarRelacoes(evento.getId());
        salvarObjetivos(evento.getId(), evento.getObjetivos());
    }

    public void deletar(Long id) {
        deletarRelacoes(id);
        String sql = "DELETE FROM evento WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar o evento.", e);
        }
    }

    private void deletarRelacoes(Long eventoId) {
        String sql = "DELETE FROM evento_objetivo WHERE evento_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, eventoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar relacoes com objetivos.", e);
        }
    }
}
