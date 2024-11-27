package br.com.nomori.flowpay.repository;

import br.com.nomori.flowpay.model.TipoEquipe;
import br.com.nomori.flowpay.model.TipoUsuario;
import br.com.nomori.flowpay.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryUsuarioRepository implements UsuarioRepository, UserDetailsService {

    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder;

    public InMemoryUsuarioRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @PostConstruct
    public void addDefaultUsers() {

        for (int i = 1; i <= 15; i++) {
            inserir(new Usuario(UUID.randomUUID().toString(),
                    "user"+i,
                    "password",
                    "nome " + i,
                    TipoUsuario.USUARIO,
                    ""
            ));
        }

        var equipes = TipoEquipe.values();
        for (int i = 1; i <= 25; i++) {
            inserir(new Usuario(UUID.randomUUID().toString(),
                    "suporte"+i,
                    "password",
                    "atendente " + 1,
                    TipoUsuario.ATENDENTE,
                    equipes[i % equipes.length].value
            ));
        }
    }

    @Override
    public Usuario buscarPorNome(String username) {
        return usuarios.values().stream().filter(u -> u.usuario().equals(username)).findAny().orElse(null);
    }

    @Override
    public Usuario buscarPorId(String usuarioId) {
        return usuarios.get(usuarioId);
    }

    @Override
    public void inserir(Usuario usuario) {
        var userCopy = new Usuario(
                usuario.uuid(),
                usuario.usuario(),
                passwordEncoder.encode(usuario.password()),
                usuario.nome(),
                usuario.tipo(),
                usuario.equipe()
        );

        usuarios.put(usuario.uuid(), userCopy);
    }

    @Override
    public List<Usuario> buscarPorEquipe(String equipe) {
        return usuarios.values()
                .stream()
                .filter((Usuario u) -> equipe.equals(u.equipe()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = buscarPorNome(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("usuário não encontrado");
        }
        return usuario;
    }
}
