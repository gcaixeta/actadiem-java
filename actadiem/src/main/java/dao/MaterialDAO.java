package dao;

import model.*;
import util.BancoDeDados;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {
    private Connection conn;

    public MaterialDAO() {
        conn = BancoDeDados.getInstance();
    }

    public Long salvar(Material material, Usuario usuario) {
        String sql = "INSERT INTO material (titulo, descricao, tipo, url, usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (var ps = conn.prepareStatement(sql, new String[]{"id"})) {
            ps.setString(1, material.getTitulo().trim());
            ps.setString(2, material.getDescricao().trim());
            ps.setInt(3, material.getTipo().getValue());
            ps.setString(4, material.getUrl() != null ? material.getUrl().trim() : null);
            ps.setLong(5, usuario.getId());

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    salvarRelacoes(id, material);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel salvar o material.", e);
        }
        return null;
    }

    private void salvarRelacoes(Long materialId, Material material) {
        salvarTarefas(materialId, material.getTarefas());
        salvarObjetivos(materialId, material.getObjetivos());
    }

    private void salvarTarefas(Long materialId, List<Tarefa> tarefas) {
        String sql = "INSERT INTO material_tarefa (material_id, tarefa_id) VALUES (?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            for (Tarefa tarefa : tarefas) {
                ps.setLong(1, materialId);
                ps.setLong(2, tarefa.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar relacoes com tarefas.", e);
        }
    }

    private void salvarObjetivos(Long materialId, List<Objetivo> objetivos) {
        String sql = "INSERT INTO material_objetivo (material_id, objetivo_id) VALUES (?, ?)";
        try (var ps = conn.prepareStatement(sql)) {
            for (Objetivo objetivo : objetivos) {
                ps.setLong(1, materialId);
                ps.setLong(2, objetivo.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar relacoes com objetivos.", e);
        }
    }

    public List<Material> listarTodos(Usuario usuario) {
        String sql = "SELECT id, titulo, descricao, tipo, url FROM material WHERE usuario_id = ? ORDER BY id";
        List<Material> materiais = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, usuario.getId());

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitulo(rs.getString("titulo"));
                    material.setDescricao(rs.getString("descricao"));
                    material.setTipo(TipoMaterial.fromValue(rs.getInt("tipo")));
                    material.setUrl(rs.getString("url"));
                    material.setTarefas(buscarTarefasPorMaterial(material.getId()));
                    material.setObjetivos(buscarObjetivosPorMaterial(material.getId()));
                    materiais.add(material);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar materiais.", e);
        }

        return materiais;
    }

    private List<Tarefa> buscarTarefasPorMaterial(Long materialId) {
        String sql = """
            SELECT t.id, t.titulo, t.descricao, t.prioridade, t.concluida, t.data_limite
            FROM tarefa t
            INNER JOIN material_tarefa mt ON mt.tarefa_id = t.id
            WHERE mt.material_id = ?
            ORDER BY t.id
        """;
        List<Tarefa> tarefas = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);

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
            throw new RuntimeException("Erro ao buscar tarefas do material.", e);
        }

        return tarefas;
    }

    private List<Objetivo> buscarObjetivosPorMaterial(Long materialId) {
        String sql = """
            SELECT o.id, o.titulo, o.descricao, o.status
            FROM objetivo o
            INNER JOIN material_objetivo mo ON mo.objetivo_id = o.id
            WHERE mo.material_id = ?
            ORDER BY o.id
        """;
        List<Objetivo> objetivos = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, materialId);

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
            throw new RuntimeException("Erro ao buscar objetivos do material.", e);
        }

        return objetivos;
    }

    public void atualizar(Material material) {
        String sql = "UPDATE material SET titulo = ?, descricao = ?, tipo = ?, url = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getTitulo().trim());
            ps.setString(2, material.getDescricao().trim());
            ps.setInt(3, material.getTipo().getValue());
            ps.setString(4, material.getUrl() != null ? material.getUrl().trim() : null);
            ps.setLong(5, material.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel atualizar o material.", e);
        }

        deletarRelacoes(material.getId());
        salvarRelacoes(material.getId(), material);
    }

    public void deletar(Long id) {
        deletarRelacoes(id);
        String sql = "DELETE FROM material WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel deletar o material.", e);
        }
    }

    private void deletarRelacoes(Long materialId) {
        String sqlTarefas = "DELETE FROM material_tarefa WHERE material_id = ?";
        try (var ps = conn.prepareStatement(sqlTarefas)) {
            ps.setLong(1, materialId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar relacoes com tarefas.", e);
        }

        String sqlObjetivos = "DELETE FROM material_objetivo WHERE material_id = ?";
        try (var ps = conn.prepareStatement(sqlObjetivos)) {
            ps.setLong(1, materialId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar relacoes com objetivos.", e);
        }
    }
}
