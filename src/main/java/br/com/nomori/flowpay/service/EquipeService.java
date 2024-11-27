package br.com.nomori.flowpay.service;

import br.com.nomori.flowpay.model.Ticket;
import br.com.nomori.flowpay.repository.TicketRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EquipeService {
    private static final int CAPACIDADE_ATENDENTE = 3;
    private final Queue<Ticket> filaTickets = new LinkedList<>();

    private final Map<String, AtomicInteger> ocupacaoAtendentes = new HashMap<>();

    private final TicketRepository ticketRepository;

    public EquipeService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void enfileirar(Ticket ticket) {
        filaTickets.add(ticket);
        atribuir();
    }

    public void registrarAtendente(String usuarioId, int ticketsAtivos) {
        ocupacaoAtendentes.put(usuarioId, new AtomicInteger(ticketsAtivos));
    }

    public void notificarConclusao(Ticket ticket) {
        AtomicInteger countAtendente = ocupacaoAtendentes.get(ticket.atendenteId());
        if (countAtendente != null) {
            countAtendente.decrementAndGet();
        }
        atribuir();
    }

    private void atribuir() {
        AtomicBoolean atribuiu = new AtomicBoolean(true);
        while (!filaTickets.isEmpty() && atribuiu.get()) {
            atribuiu.set(false);
            ocupacaoAtendentes.entrySet().stream()
                    .filter(entry -> entry.getValue().get() < CAPACIDADE_ATENDENTE)
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().get()))
                    .forEach(entry -> {
                        if (filaTickets.isEmpty()) {
                            return;
                        }
                        Ticket ticket = filaTickets.remove();
                        ticketRepository.atribuirAtendente(ticket.id(), entry.getKey());
                        entry.getValue().incrementAndGet();
                    });
        }
    }
}
