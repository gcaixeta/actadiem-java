package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer nota;
    private String comentario;
    private LocalDate data;
    private List<Objetivo> objetivos;

    public Evento(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.objetivos = new ArrayList<>();
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

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getDate() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Objetivo> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<Objetivo> objetivos) {
        this.objetivos = objetivos;
    }
}
