package controller;

import dao.EventoDAO;
import model.Evento;

public class EventoController {

    private final EventoDAO eventoDAO;

    public EventoController(EventoDAO dao) {
        eventoDAO = dao;
    }

    public boolean registrarEvento(Evento evento) {
        Long id = eventoDAO.salvar(evento);
        return id != null;
    }
}
