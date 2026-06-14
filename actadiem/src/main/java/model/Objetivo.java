package model;

public class Objetivo {
    private Long id;
    private String titulo;
    private String descricao;
    private StatusObjetivo status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusObjetivo getStatus() {
        return status;
    }

    public void setStatus(StatusObjetivo status) {
        this.status = status;
    }
}
