package controller;

import dao.HabitoDAO;
import model.Habito;

import java.util.List;

public class HabitoController {
    private final HabitoDAO habitoDAO;

    public HabitoController(HabitoDAO dao) {
        this.habitoDAO = dao;
    }

    public void salvar(Habito habito) {
        habitoDAO.salvar(habito, UsuarioController.getUsuarioAtivo());
    }

    public List<Habito> listarTodos() {
        return habitoDAO.listarTodos(UsuarioController.getUsuarioAtivo());
    }

    public void atualizar(Habito habito) {
        habitoDAO.atualizar(habito);
    }

    public void deletar(Long id) {
        habitoDAO.deletar(id);
    }
}
