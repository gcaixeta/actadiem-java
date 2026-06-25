package controller;

import dao.EventoDAO;
import model.Evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluxo da Revisao Diaria: percorre os eventos do dia anterior, um por vez,
 * permitindo dar uma nota de 1 a 5 e um comentario opcional para cada um.
 *
 * Monta a fila com os eventos de ontem e mantem o indice da atividade atual;
 * a tela chama {@link #avaliar(int, String)} para registrar a nota e avancar.
 */
public class RevisaoDiariaController {
    private final EventoDAO eventoDAO;

    private List<Evento> fila = new ArrayList<>();
    private int indiceAtual = -1;

    private int avaliados = 0;
    private int somaNotas = 0;

    public RevisaoDiariaController(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    /** Monta a fila com os eventos do dia anterior. */
    public void iniciar() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        fila = eventoDAO.listarPorData(ontem, UsuarioController.getUsuarioAtivo());
        indiceAtual = fila.isEmpty() ? -1 : 0;
        avaliados = 0;
        somaNotas = 0;
    }

    public boolean temEvento() {
        return indiceAtual >= 0 && indiceAtual < fila.size();
    }

    public Evento getAtual() {
        if (!temEvento()) {
            return null;
        }
        return fila.get(indiceAtual);
    }

    /** Posicao 1-based da atividade atual, para exibir "x de n". */
    public int getPosicao() {
        return indiceAtual + 1;
    }

    public int getTotal() {
        return fila.size();
    }

    /** Registra a nota (1-5) e o comentario do evento atual e avanca a fila. */
    public void avaliar(int nota, String comentario) {
        Evento atual = getAtual();
        if (atual == null) {
            return;
        }
        atual.setNota(nota);
        atual.setComentario(comentario);
        eventoDAO.avaliar(atual.getId(), nota, comentario);

        avaliados++;
        somaNotas += nota;
        indiceAtual++;
    }

    public boolean isFim() {
        return indiceAtual >= fila.size();
    }

    public int getAvaliados() {
        return avaliados;
    }

    public double getNotaMedia() {
        return avaliados == 0 ? 0.0 : (double) somaNotas / avaliados;
    }
}
