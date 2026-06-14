package dao;

import model.Prioridade;
import model.Tarefa;
import model.Usuario;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {
    private Connection conn;

    public TarefaDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Tarefa tarefa, Usuario usuario) {
        String sql = "INSERT INTO tarefa (titulo, descricao, prioridade, concluida, data_limite, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, tarefa.getTitulo().trim());
            ps.setString(2, tarefa.getDescricao().trim());
            ps.setInt(3, tarefa.getPrioridade().getValue());
            ps.setBoolean(4, tarefa.isConcluida());

            if (tarefa.getDataLimite() != null) {
                ps.setDate(5, new Date(tarefa.getDataLimite().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setLong(6, usuario.getId());

            return (long) ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar a tarefa.", e);
        }
    }

    public List<Tarefa> listarTodos(Usuario usuario) {
        String sql = "SELECT id, titulo, descricao, prioridade, concluida, data_limite FROM tarefa WHERE usuario_id = ? ORDER BY id";
        List<Tarefa> tarefas = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuario.getId());

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarefa tarefa = new Tarefa();
                    tarefa.setId(rs.getLong("id"));
                    tarefa.setTitulo(rs.getString("titulo"));
                    tarefa.setDescricao(rs.getString("descricao"));
                    tarefa.setPrioridade(Prioridade.fromValue(rs.getInt("prioridade")));
                    tarefa.setConcluida(rs.getBoolean("concluida"));

                    Date dataLimite = rs.getDate("data_limite");
                    if (dataLimite != null) {
                        tarefa.setDataLimite(new java.util.Date(dataLimite.getTime()));
                    }

                    tarefas.add(tarefa);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas.", e);
        }

        return tarefas;
    }

    public void atualizar(Tarefa tarefa) {
        String sql = "UPDATE tarefa SET titulo = ?, descricao = ?, prioridade = ?, concluida = ?, data_limite = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, tarefa.getTitulo().trim());
            ps.setString(2, tarefa.getDescricao().trim());
            ps.setInt(3, tarefa.getPrioridade().getValue());
            ps.setBoolean(4, tarefa.isConcluida());

            if (tarefa.getDataLimite() != null) {
                ps.setDate(5, new Date(tarefa.getDataLimite().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setLong(6, tarefa.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel atualizar a tarefa.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM tarefa WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar a tarefa.", e);
        }
    }
}
