package br.com.nomori.flowpay.model;

import java.time.LocalDateTime;
import java.util.List;

public record Ticket(
        String id,
        String clienteId,
        String equipeId,
        String atendenteId,
        String assunto,
        LocalDateTime dataCriacao,
        StatusTicket status,

        List<Mensagem> mensagens
) {
}
