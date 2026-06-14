package controller;

import dao.EventoDAO;
import model.Evento;

public class EventoController {

    private final EventoDAO eventoDAO;

    public EventoController(EventoDAO dao) {
        eventoDAO = dao;
    }

    public void registrarEvento(Evento evento) {
        Long id = eventoDAO.salvar(evento, UsuarioController.getUsuarioAtivo());
    }
}
