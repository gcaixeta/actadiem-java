package dao;

import model.Objetivo;
import model.StatusObjetivo;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjetivoDAO {
    private Connection conn;

    public ObjetivoDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Objetivo objetivo, Usuario usuario) {
        String sql = "INSERT INTO objetivo (titulo, descricao, status, usuario_id) VALUES (?, ?, ?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, objetivo.getTitulo().trim());
            ps.setString(2, objetivo.getDescricao().trim());
            ps.setInt(3, objetivo.getStatus().getvalue());
            ps.setLong(4, usuario.getId());

            return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o objetivo.", e);
        }
    }

    public List<Objetivo> listarTodos(Usuario usuario) {
        String sql = "SELECT id, titulo, descricao, status FROM objetivo WHERE usuario_id = ? ORDER BY id";
        List<Objetivo> objetivos = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuario.getId());

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
            throw new RuntimeException("Erro ao listar objetivos.", e);
        }

        return objetivos;
    }

    public void atualizar(Objetivo objetivo) {
        String sql = "UPDATE objetivo SET titulo = ?, descricao = ?, status = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, objetivo.getTitulo().trim());
            ps.setString(2, objetivo.getDescricao().trim());
            ps.setInt(3, objetivo.getStatus().getvalue());
            ps.setLong(4, objetivo.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel atualizar o objetivo.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM objetivo WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar o objetivo.", e);
        }
    }
}
