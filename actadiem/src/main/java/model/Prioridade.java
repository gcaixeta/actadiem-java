package model;

public enum Prioridade {
    BAIXA(1),
    MEDIA(2),
    GRANDE(3);

    private final int value;

    Prioridade(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Prioridade fromValue(int value) {
        for (Prioridade prioridade : values()) {
            if (prioridade.value == value) {
                return prioridade;
            }
        }

        throw new IllegalArgumentException("Prioridade inválida: " + value);
    }
}


