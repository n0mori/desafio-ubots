package br.com.nomori.flowpay.model;

import java.time.LocalDateTime;

public record Mensagem(String uuid, String autorId, String atendimentoId, String texto, LocalDateTime data) {
}
