package controller;

import dao.MaterialDAO;
import model.Material;

import java.util.List;

public class MaterialController {
    private final MaterialDAO materialDAO;

    public MaterialController(MaterialDAO dao) {
        this.materialDAO = dao;
    }

    public void salvar(Material material) {
        materialDAO.salvar(material, UsuarioController.getUsuarioAtivo());
    }

    public List<Material> listarTodos() {
        return materialDAO.listarTodos(UsuarioController.getUsuarioAtivo());
    }

    public void atualizar(Material material) {
        materialDAO.atualizar(material);
    }

    public void deletar(Long id) {
        materialDAO.deletar(id);
    }
}
