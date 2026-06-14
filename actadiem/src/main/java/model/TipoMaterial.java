package model;

public enum TipoMaterial {
    VIDEO(1),
    LIVRO(2),
    EBOOK(3),
    ARTIGO(4),
    PODCAST(5),
    DOCUMENTACAO(6),
    OUTRO(7);

    private final int value;

    TipoMaterial(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TipoMaterial fromValue(int value) {
        for (TipoMaterial tipo : values()) {
            if (tipo.value == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("TipoMaterial invalido: " + value);
    }
}
