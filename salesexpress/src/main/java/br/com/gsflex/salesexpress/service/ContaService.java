package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.dto.request.ContaRequestDto;
import br.com.gsflex.salesexpress.entity.Usuario;
import br.com.gsflex.salesexpress.exception.BusinessException;
import br.com.gsflex.salesexpress.exception.ResourceNotFoundException;
import br.com.gsflex.salesexpress.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContaService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    public void alterar(String usernameAtual, ContaRequestDto request) {

        Usuario usuario = repository.findByUsername(usernameAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!encoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        String novoUsuario = request.novoUsuario() == null ? "" : request.novoUsuario().trim();
        boolean trocaUsuario = !novoUsuario.isEmpty() && !novoUsuario.equals(usuario.getUsername());
        boolean trocaSenha = request.novaSenha() != null && !request.novaSenha().isEmpty();

        if (!trocaUsuario && !trocaSenha) {
            throw new BusinessException("Informe um novo usuário ou uma nova senha");
        }

        if (trocaUsuario) {
            if (novoUsuario.length() < 3) {
                throw new BusinessException("O usuário deve ter ao menos 3 caracteres");
            }
            if (repository.existsByUsernameIgnoreCase(novoUsuario)) {
                throw new BusinessException("Este usuário já existe");
            }
            usuario.setUsername(novoUsuario);
        }

        if (trocaSenha) {
            usuario.setSenha(encoder.encode(request.novaSenha()));
        }

        repository.save(usuario);
    }
}
