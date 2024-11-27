package br.com.nomori.flowpay.dto;

public record RequestFechamentoDTO(
        boolean resolvido,
        String mensagem
) {
}
