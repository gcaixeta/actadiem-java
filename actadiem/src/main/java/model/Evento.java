package model;

import java.time.LocalDate;

public class Evento {
    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate data;

    public Evento(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }
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

    public LocalDate getDate() {
        return data;
    }
}
