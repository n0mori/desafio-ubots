package br.com.nomori.flowpay.service;

import br.com.nomori.flowpay.dto.RequestAtendimentoDTO;
import br.com.nomori.flowpay.dto.RequestFechamentoDTO;
import br.com.nomori.flowpay.model.*;
import br.com.nomori.flowpay.repository.TicketRepository;
import br.com.nomori.flowpay.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AtendimentoService {

    private final TicketRepository ticketRepository;

    private final UsuarioRepository usuarioRepository;

    private final Map<TipoEquipe, EquipeService> equipes;

    public AtendimentoService(TicketRepository ticketRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        equipes = new HashMap<>();
        registrarEquipes();
    }

    private void registrarEquipes() {
        for (TipoEquipe te : TipoEquipe.values()) {
            equipes.put(te, new EquipeService(ticketRepository));
        }
        equipes.forEach((key, value) -> usuarioRepository.buscarPorEquipe(key.value).forEach(usuario -> {
            var tickets = ticketRepository.buscarPorAtendente(usuario.uuid(), true);
            value.registrarAtendente(usuario.uuid(), tickets.size());
        }));
    }


    public Ticket iniciarAtendimento(RequestAtendimentoDTO dto, String usuario) {
        String atendimentoId = UUID.randomUUID().toString();
        var tipoEquipe = TipoEquipe.selecionarAssunto(dto.assunto());
        var ticket = new Ticket(
                atendimentoId,
                usuario,
                tipoEquipe.value,
                null,
                dto.assunto(),
                LocalDateTime.now(),
                StatusTicket.PENDENTE,
                List.of(
                        new Mensagem(
                                UUID.randomUUID().toString(),
                                usuario,
                                atendimentoId,
                                dto.mensagem(),
                                LocalDateTime.now()
                        )
                )
        );
        ticketRepository.inserirAtendimento(ticket);
        equipes.get(tipoEquipe).enfileirar(ticket);
        return ticket;
    }

    public Ticket adicionarMensagem(String atendimentoId, String usuarioId, String texto) {
        Ticket ticket = ticketRepository.buscarPorId(atendimentoId);
        if (ticket == null) {
            return null;
        }
        if (!usuarioId.equals(ticket.atendenteId()) && !usuarioId.equals(ticket.clienteId())) {
            return null;
        }

        Mensagem mensagem = new Mensagem(UUID.randomUUID().toString(),
                usuarioId,
                atendimentoId,
                texto,
                LocalDateTime.now());
        return ticketRepository.inserirMensagem(atendimentoId, mensagem);
    }

    public Ticket finalizarAtendimento(String atendimentoId, String usuarioId, RequestFechamentoDTO resolvido) {
        Ticket ticket = ticketRepository.buscarPorId(atendimentoId);
        if (ticket == null) {
            return null;
        }

        if (!usuarioId.equals(ticket.clienteId()) && !usuarioId.equals(ticket.atendenteId())) {
            return null;
        }

        StatusTicket novoEstado = resolvido.resolvido() ? StatusTicket.RESOLVIDO : StatusTicket.CANCELADO;

        ticket = ticketRepository.alterarStatus(atendimentoId, novoEstado);

        var equipe = equipes.get(TipoEquipe.porValor(ticket.equipeId()));
        equipe.notificarConclusao(ticket);
        return ticket;
    }

    public List<Ticket> buscarPorUsuario(String usuarioId, boolean filtrarAtivos) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        return switch (usuario.tipo()) {
            case USUARIO -> ticketRepository.buscarPorCliente(usuarioId, filtrarAtivos);
            case ATENDENTE -> ticketRepository.buscarPorAtendente(usuarioId, filtrarAtivos);
        };
    }

    public Ticket buscarPorId(String atendimentoId, String usuarioId) {
        Ticket ticket = ticketRepository.buscarPorId(atendimentoId);
        if (ticket == null) {
            return null;
        }
        if (!usuarioId.equals(ticket.atendenteId()) && !usuarioId.equals(ticket.clienteId())) {
            return null;
        }

        return ticket;
    }
}
