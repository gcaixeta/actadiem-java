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
}
