package model;

public class Habito {
    private Long id;
    private String nome;
    private Frequencia frequencia;

    public class Frequencia {
        private int quantidade;
        private UnidadeFrequencia unidade;
    }
}
