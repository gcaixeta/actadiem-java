package model;

public enum StatusObjetivo {
    EM_PLANEJAMENTO(1),
    PLANEJADO(2),
    EM_EXECUCAO(3),
    ALCANCADO(4),
    NAO_ALCANCADO(5);

    private final int value;

    StatusObjetivo(int value) {
        this.value = value;
    }

    public int getvalue() {
        return value;
    }

    public static StatusObjetivo fromValue(int value) {
        for (StatusObjetivo status: values()) {
            if (status.value == value) {
                return status;
            }
        }

        throw new IllegalArgumentException("StatusObjetivo nao encontrado para valor " + value);
    }
}
