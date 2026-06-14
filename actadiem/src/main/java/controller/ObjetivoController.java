package controller;

import dao.ObjetivoDAO;
import model.Objetivo;

import java.util.List;

public class ObjetivoController {
    private final ObjetivoDAO objetivoDAO;

    public ObjetivoController(ObjetivoDAO dao) {
        this.objetivoDAO = dao;
    }

    public void salvar(Objetivo objetivo) {
        objetivoDAO.salvar(objetivo, UsuarioController.getUsuarioAtivo());
    }

    public List<Objetivo> listarTodos() {
        return objetivoDAO.listarTodos(UsuarioController.getUsuarioAtivo());
    }

    public void atualizar(Objetivo objetivo) {
        objetivoDAO.atualizar(objetivo);
    }

    public void deletar(Long id) {
        objetivoDAO.deletar(id);
    }
}
