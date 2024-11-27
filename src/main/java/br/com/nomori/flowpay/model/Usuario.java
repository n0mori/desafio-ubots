package br.com.nomori.flowpay.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record Usuario(
        String uuid,
        String usuario,
        String password,
        String nome,
        TipoUsuario tipo,
        String equipe
) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (tipo) {
            case USUARIO -> List.of(new SimpleGrantedAuthority("CRIAR_TICKET"));
            case ATENDENTE -> List.of(new SimpleGrantedAuthority("FECHAR_TICKET"));
        };
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return usuario;
    }
}
