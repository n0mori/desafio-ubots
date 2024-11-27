package br.com.nomori.flowpay.repository;

import br.com.nomori.flowpay.model.Usuario;

import java.util.List;

public interface UsuarioRepository {
    Usuario buscarPorNome(String username);

    Usuario buscarPorId(String usuarioId);

    void inserir(Usuario usuario);

    List<Usuario> buscarPorEquipe(String equipe);
}
