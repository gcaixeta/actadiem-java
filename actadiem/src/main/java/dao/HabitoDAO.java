package dao;

import model.Habito;
import model.UnidadeFrequencia;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HabitoDAO {
    private Connection conn;

    public HabitoDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Habito habito, Usuario usuario) {
        String sql = "INSERT INTO habito (nome, frequencia_quantidade, frequencia_unidade, usuario_id) VALUES (?, ?, ?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, habito.getNome().trim());
            ps.setInt(2, habito.getFrequencia().getQuantidade());
            ps.setInt(3, habito.getFrequencia().getUnidade().getValue());
            ps.setLong(4, usuario.getId());

            return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o habito.", e);
        }
    }

    public List<Habito> listarTodos(Usuario usuario) {
        String sql = "SELECT id, nome, frequencia_quantidade, frequencia_unidade FROM habito WHERE usuario_id = ? ORDER BY id";
        List<Habito> habitos = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuario.getId());

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habito habito = new Habito(rs.getString("nome"));
                    habito.setId(rs.getLong("id"));
                    habito.getFrequencia().setQuantidade(rs.getInt("frequencia_quantidade"));
                    habito.getFrequencia().setUnidade(UnidadeFrequencia.fromValue(rs.getInt("frequencia_unidade")));
                    habitos.add(habito);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar habitos.", e);
        }

        return habitos;
    }

    public void atualizar(Habito habito) {
        String sql = "UPDATE habito SET nome = ?, frequencia_quantidade = ?, frequencia_unidade = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, habito.getNome().trim());
            ps.setInt(2, habito.getFrequencia().getQuantidade());
            ps.setInt(3, habito.getFrequencia().getUnidade().getValue());
            ps.setLong(4, habito.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel atualizar o habito.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM habito WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar o habito.", e);
        }
    }
}
