package br.com.nomori.flowpay.repository;

import br.com.nomori.flowpay.model.Mensagem;
import br.com.nomori.flowpay.model.StatusTicket;
import br.com.nomori.flowpay.model.Ticket;

import java.util.List;

public interface TicketRepository {
    void inserirAtendimento(Ticket ticket);

    void atribuirAtendente(String ticketId, String atendenteId);

    Ticket buscarPorId(String ticketId);

    List<Ticket> buscarPorCliente(String clienteId, boolean filtrarAtivos);

    List<Ticket> buscarPorEquipe(String equipeId, boolean filtrarAtivos);

    List<Ticket> buscarPorAtendente(String atendenteId, boolean filtrarAtivos);

    List<Ticket> buscarPendentes();

    Ticket inserirMensagem(String ticketId, Mensagem mensagem);

    Ticket alterarStatus(String ticketId, StatusTicket status);
}
