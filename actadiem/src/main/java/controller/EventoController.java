package controller;

import dao.EventoDAO;
import model.Evento;

import java.time.LocalDate;
import java.util.List;

public class EventoController {

    private final EventoDAO eventoDAO;

    public EventoController(EventoDAO dao) {
        eventoDAO = dao;
    }

    public void registrarEvento(Evento evento) {
        eventoDAO.salvar(evento, UsuarioController.getUsuarioAtivo());
    }

    public List<Evento> listarEventosHoje() {
        return eventoDAO.listarPorData(LocalDate.now(), UsuarioController.getUsuarioAtivo());
    }

    public void atualizar(Evento evento) {
        eventoDAO.atualizar(evento);
    }

    public void deletar(Long id) {
        eventoDAO.deletar(id);
    }
}
