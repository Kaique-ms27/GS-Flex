package br.com.gsflex.salesexpress.config;

import br.com.gsflex.salesexpress.entity.Usuario;
import br.com.gsflex.salesexpress.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro usuário quando a tabela está vazia, usando
 * app.admin.usuario / app.admin.senha do application.properties.
 */
@Component
public class AdminInicializador implements ApplicationRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final String usuario;
    private final String senha;

    public AdminInicializador(UsuarioRepository repository,
                              PasswordEncoder encoder,
                              @Value("${app.admin.usuario:admin}") String usuario,
                              @Value("${app.admin.senha:admin123}") String senha) {
        this.repository = repository;
        this.encoder = encoder;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            Usuario u = new Usuario();
            u.setUsername(usuario);
            u.setSenha(encoder.encode(senha));
            repository.save(u);
        }
    }
}
