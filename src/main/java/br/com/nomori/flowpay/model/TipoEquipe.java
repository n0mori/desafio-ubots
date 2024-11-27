package br.com.nomori.flowpay.model;

public enum TipoEquipe {
    CARTAO("cartoes", "Cartões"),
    EMPRESTIMO("emprestimo", "Empréstimos"),
    OUTROS("outros", "Outros assuntos");

    public final String value;
    public final String label;

    TipoEquipe(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static TipoEquipe selecionarAssunto(String assunto) {
        return switch (assunto) {
            case "Problemas com cartão" -> CARTAO;
            case "contratação de empréstimo" -> EMPRESTIMO;
            default -> OUTROS;
        };
    }

    public static TipoEquipe porValor(String equipeId) {
        for (TipoEquipe equipe : values()) {
            if (equipe.value.equals(equipeId)) {
                return equipe;
            }
        }
        return null;
    }
}
