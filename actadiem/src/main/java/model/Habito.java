package model;

public class Habito {
    private Long id;
    private String nome;
    private Frequencia frequencia;

    public Habito() {
        this.frequencia = new Frequencia();
    }

    public Habito(String nome) {
        this();
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Frequencia getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Frequencia frequencia) {
        this.frequencia = frequencia;
    }

    public static class Frequencia {
        private int quantidade;
        private UnidadeFrequencia unidade;

        public Frequencia() {}

        public Frequencia(int quantidade, UnidadeFrequencia unidade) {
            this.quantidade = quantidade;
            this.unidade = unidade;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public UnidadeFrequencia getUnidade() {
            return unidade;
        }

        public void setUnidade(UnidadeFrequencia unidade) {
            this.unidade = unidade;
        }
    }
}
