package br.com.nomori.flowpay.controller;

import br.com.nomori.flowpay.service.AtendimentoService;
import br.com.nomori.flowpay.dto.RequestAtendimentoDTO;
import br.com.nomori.flowpay.dto.RequestFechamentoDTO;
import br.com.nomori.flowpay.dto.RequestMensagemDTO;
import br.com.nomori.flowpay.model.Ticket;
import br.com.nomori.flowpay.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/atendimento")
public class AtendimentoController {

    final AtendimentoService atendimentoService;

    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PostMapping("")
    public ResponseEntity<Ticket> postAtendimento(Authentication authentication, @RequestBody RequestAtendimentoDTO request) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ticket ticket = atendimentoService.iniciarAtendimento(request, usuario.uuid());

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("")
    public ResponseEntity<List<Ticket>> buscarAtendimentos(@RequestParam(required = false, defaultValue = "false") boolean filtrarAtivos,
                                           Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(atendimentoService.buscarPorUsuario(usuario.uuid(), filtrarAtivos));
    }

    @GetMapping("/{atendimentoId}")
    public ResponseEntity<Ticket> buscarAtendimento(@PathVariable("atendimentoId") String atendimentoId,
                                                    Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ticket ticket = atendimentoService.buscarPorId(atendimentoId, usuario.uuid());
        return ResponseEntity.ofNullable(ticket);
    }

    @PostMapping("/{atendimentoId}/mensagem")
    public ResponseEntity<Ticket> enviarMensagem(@PathVariable("atendimentoId") String atendimentoId,
                                                 @RequestBody RequestMensagemDTO mensagem,
                                                 Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Ticket ticket = atendimentoService.adicionarMensagem(atendimentoId, usuario.uuid(), mensagem.texto());
        return ResponseEntity.ofNullable(ticket);
    }

    @PostMapping("/{atendimentoId}/finalizar")
    public ResponseEntity<Ticket> finalizarAtendimento(@PathVariable("atendimentoId") String atendimentoId,
                                                       @RequestBody RequestFechamentoDTO request,
                                                       Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ofNullable(atendimentoService.finalizarAtendimento(atendimentoId, usuario.uuid(), request));
    }
}
