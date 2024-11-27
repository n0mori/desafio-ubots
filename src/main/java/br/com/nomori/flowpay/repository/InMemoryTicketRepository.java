package br.com.nomori.flowpay.repository;

import br.com.nomori.flowpay.model.Mensagem;
import br.com.nomori.flowpay.model.StatusTicket;
import br.com.nomori.flowpay.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryTicketRepository implements TicketRepository {

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    @Override
    public void inserirAtendimento(Ticket ticket) {
        tickets.put(ticket.id(), ticket);
    }

    @Override
    public void atribuirAtendente(String ticketId, String atendenteId) {
        var ticket = buscarPorId(ticketId);
        if (ticket == null) {
            return;
        }

        ticket = new Ticket(
                ticket.id(),
                ticket.clienteId(),
                ticket.equipeId(),
                atendenteId,
                ticket.assunto(),
                ticket.dataCriacao(),
                ticket.status(),
                ticket.mensagens()
        );
        inserirAtendimento(ticket);
    }

    public Ticket inserirMensagem(String ticketId, Mensagem mensagem) {
        var ticket = buscarPorId(ticketId);
        if (ticket == null) {
            return null;
        }

        var mensagens = new ArrayList<>(ticket.mensagens());
        mensagens.add(mensagem);

        ticket = new Ticket(
                ticket.id(),
                ticket.clienteId(),
                ticket.equipeId(),
                ticket.atendenteId(),
                ticket.assunto(),
                ticket.dataCriacao(),
                ticket.status(),
                mensagens
        );
        inserirAtendimento(ticket);
        return ticket;
    }

    @Override
    public Ticket alterarStatus(String ticketId, StatusTicket status) {
        var ticket = buscarPorId(ticketId);
        if (ticket == null) {
            return null;
        }

        ticket = new Ticket(
                ticket.id(),
                ticket.clienteId(),
                ticket.equipeId(),
                ticket.atendenteId(),
                ticket.assunto(),
                ticket.dataCriacao(),
                status,
                ticket.mensagens()
        );
        inserirAtendimento(ticket);
        return ticket;
    }

    @Override
    public Ticket buscarPorId(String ticketId) {
        return tickets.get(ticketId);
    }

    @Override
    public List<Ticket> buscarPorCliente(String clienteId, boolean filtrarAtivos) {
        return tickets.values()
                .stream()
                .filter(ticket -> clienteId.equals(ticket.clienteId()))
                .filter(ticket -> !filtrarAtivos || StatusTicket.ATIVO.equals(ticket.status()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> buscarPorEquipe(String equipeId, boolean filtrarAtivos) {
        return tickets.values()
                .stream()
                .filter(ticket -> equipeId.equals(ticket.equipeId()))
                .filter(ticket -> !filtrarAtivos || StatusTicket.ATIVO.equals(ticket.status()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> buscarPorAtendente(String atendenteId, boolean filtrarAtivos) {
        return tickets.values()
                .stream()
                .filter(ticket -> atendenteId.equals(ticket.atendenteId()))
                .filter(ticket -> !filtrarAtivos || StatusTicket.ATIVO.equals(ticket.status()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> buscarPendentes() {
        return null;
    }
}
