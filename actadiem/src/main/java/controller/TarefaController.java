package controller;

import dao.TarefaDAO;
import model.Tarefa;

import java.util.List;

public class TarefaController {
    private final TarefaDAO tarefaDAO;

    public TarefaController(TarefaDAO dao) {
        this.tarefaDAO = dao;
    }

    public void salvar(Tarefa tarefa) {
        tarefaDAO.salvar(tarefa, UsuarioController.getUsuarioAtivo());
    }

    public List<Tarefa> listarTodos() {
        return tarefaDAO.listarTodos(UsuarioController.getUsuarioAtivo());
    }

    public void atualizar(Tarefa tarefa) {
        tarefaDAO.atualizar(tarefa);
    }

    public void deletar(Long id) {
        tarefaDAO.deletar(id);
    }
}
