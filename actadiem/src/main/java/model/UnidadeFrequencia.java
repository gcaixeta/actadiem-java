package model;

public enum UnidadeFrequencia {
    DIA(1),
    SEMANA(2),
    MES(3),
    ANO(4);

    private final int value;

    UnidadeFrequencia(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UnidadeFrequencia fromValue(int value) {
        for (UnidadeFrequencia unidade : values()) {
            if (unidade.value == value) {
                return unidade;
            }
        }

        throw new IllegalArgumentException("Unidade de frequencia " + value + " nao encontrada.");
    }
}
