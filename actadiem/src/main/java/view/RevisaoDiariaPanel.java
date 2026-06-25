package view;

import controller.RevisaoDiariaController;
import dao.EventoDAO;
import model.Evento;
import model.Objetivo;

import javax.swing.*;
import java.awt.*;

/**
 * Tela da Revisao Diaria. Tres momentos num CardLayout: inicio (chamada para
 * comecar), atividade (loop sobre os eventos de ontem, dando nota 1-5 e um
 * comentario opcional) e fim (resumo da revisao). O controller guarda a fila
 * e o estado do percurso.
 */
public class RevisaoDiariaPanel extends JPanel {
    private static final Color NOTA_SELECIONADA = new Color(70, 130, 180);

    private final RevisaoDiariaController controller;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private JLabel progressoLabel;
    private JLabel tituloEventoLabel;
    private JTextArea descricaoArea;
    private JLabel objetivosLabel;
    private JButton[] notaButtons;
    private JTextArea comentarioArea;
    private JLabel resumoLabel;

    private int notaSelecionada;

    public RevisaoDiariaPanel() {
        controller = new RevisaoDiariaController(new EventoDAO());
        setLayout(new BorderLayout(10, 10));

        add(criarPainelTitulo(), BorderLayout.NORTH);

        cards.add(criarCardInicio(), "inicio");
        cards.add(criarCardAtividade(), "atividade");
        cards.add(criarCardFim(), "fim");
        add(cards, BorderLayout.CENTER);

        cardLayout.show(cards, "inicio");
    }

    private JPanel criarPainelTitulo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Revisão Diária");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label);
        return panel;
    }

    private JPanel criarCardInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel chamada = new JLabel("Vamos revisar os eventos de ontem, um de cada vez.");
        chamada.setFont(chamada.getFont().deriveFont(14f));
        gbc.gridy = 0;
        panel.add(chamada, gbc);

        JButton iniciar = new JButton("Iniciar revisão");
        iniciar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iniciar.addActionListener(e -> iniciarRevisao());
        gbc.gridy = 1;
        panel.add(iniciar, gbc);

        return panel;
    }

    private JPanel criarCardAtividade() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        progressoLabel = new JLabel();
        progressoLabel.setFont(progressoLabel.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(progressoLabel, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        tituloEventoLabel = new JLabel();
        tituloEventoLabel.setFont(tituloEventoLabel.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridy = 0;
        centro.add(tituloEventoLabel, gbc);

        descricaoArea = new JTextArea(4, 30);
        descricaoArea.setEditable(false);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);
        descricaoArea.setOpaque(false);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        centro.add(new JScrollPane(descricaoArea), gbc);

        objetivosLabel = new JLabel();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        centro.add(objetivosLabel, gbc);

        gbc.gridy = 3;
        centro.add(criarPainelNota(), gbc);

        comentarioArea = new JTextArea(4, 30);
        comentarioArea.setLineWrap(true);
        comentarioArea.setWrapStyleWord(true);
        JScrollPane comentarioScroll = new JScrollPane(comentarioArea);
        comentarioScroll.setBorder(BorderFactory.createTitledBorder("Comentário (opcional)"));
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        centro.add(comentarioScroll, gbc);

        panel.add(centro, BorderLayout.CENTER);

        JButton proximoButton = new JButton("Próximo");
        proximoButton.addActionListener(e -> avancar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        botoes.add(proximoButton);
        panel.add(botoes, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelNota() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painel.add(new JLabel("Nota:"));

        notaButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            int nota = i + 1;
            JButton botao = new JButton(String.valueOf(nota));
            botao.setFocusPainted(false);
            botao.addActionListener(e -> selecionarNota(nota));
            notaButtons[i] = botao;
            painel.add(botao);
        }
        return painel;
    }

    private JPanel criarCardFim() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel concluido = new JLabel("Revisão concluída!");
        concluido.setFont(concluido.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridy = 0;
        panel.add(concluido, gbc);

        resumoLabel = new JLabel();
        gbc.gridy = 1;
        panel.add(resumoLabel, gbc);

        JButton voltar = new JButton("Voltar ao início");
        voltar.addActionListener(e -> cardLayout.show(cards, "inicio"));
        gbc.gridy = 2;
        panel.add(voltar, gbc);

        return panel;
    }

    private void iniciarRevisao() {
        controller.iniciar();
        if (!controller.temEvento()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não há eventos de ontem para revisar.",
                    "Revisão Diária",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        atualizarTela();
    }

    private void selecionarNota(int nota) {
        notaSelecionada = nota;
        for (int i = 0; i < notaButtons.length; i++) {
            boolean escolhida = (i + 1) == nota;
            notaButtons[i].setBackground(escolhida ? NOTA_SELECIONADA : null);
            notaButtons[i].setForeground(escolhida ? Color.WHITE : null);
        }
    }

    private void avancar() {
        if (notaSelecionada == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Dê uma nota de 1 a 5 para este evento.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        controller.avaliar(notaSelecionada, comentarioArea.getText().trim());
        atualizarTela();
    }

    /** Redesenha a tela conforme o estado do controller: proximo evento ou fim. */
    private void atualizarTela() {
        if (controller.isFim()) {
            resumoLabel.setText(String.format(
                    "<html>%d evento(s) avaliado(s) &nbsp;|&nbsp; nota média %.1f</html>",
                    controller.getAvaliados(), controller.getNotaMedia()));
            cardLayout.show(cards, "fim");
            return;
        }

        Evento atual = controller.getAtual();
        progressoLabel.setText("Evento " + controller.getPosicao() + " de " + controller.getTotal());
        tituloEventoLabel.setText(atual.getTitulo());
        descricaoArea.setText(atual.getDescricao() != null ? atual.getDescricao() : "");
        objetivosLabel.setText(textoObjetivos(atual));

        // Pre-preenche se o evento ja tiver sido avaliado antes.
        if (atual.getNota() != null) {
            selecionarNota(atual.getNota());
        } else {
            limparNota();
        }
        comentarioArea.setText(atual.getComentario() != null ? atual.getComentario() : "");

        cardLayout.show(cards, "atividade");
    }

    private void limparNota() {
        notaSelecionada = 0;
        for (JButton botao : notaButtons) {
            botao.setBackground(null);
            botao.setForeground(null);
        }
    }

    private String textoObjetivos(Evento evento) {
        if (evento.getObjetivos() == null || evento.getObjetivos().isEmpty()) {
            return " ";
        }
        StringBuilder sb = new StringBuilder("Objetivos: ");
        for (int i = 0; i < evento.getObjetivos().size(); i++) {
            if (i > 0) sb.append(", ");
            Objetivo objetivo = evento.getObjetivos().get(i);
            sb.append(objetivo.getTitulo());
        }
        return sb.toString();
    }
}
