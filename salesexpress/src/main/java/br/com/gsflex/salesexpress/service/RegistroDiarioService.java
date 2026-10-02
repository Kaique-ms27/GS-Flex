package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistroDiarioService {

    private final RegistroDiarioRepository repository;


}
