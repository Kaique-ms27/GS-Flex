package br.com.gsflex.salesexpress.controller;

import br.com.gsflex.salesexpress.dto.request.ContaRequestDto;
import br.com.gsflex.salesexpress.dto.response.ContaResponseDto;
import br.com.gsflex.salesexpress.service.ContaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/conta")
public class ContaController {

    private final ContaService service;

    // Também serve para o front saber se a sessão está ativa (401 se não estiver)
    @GetMapping
    public ContaResponseDto conta(Authentication auth) {
        return new ContaResponseDto(auth.getName());
    }

    @PutMapping
    public Map<String, String> alterar(Authentication auth,
                                       @Valid @RequestBody ContaRequestDto request,
                                       HttpServletRequest http) {
        service.alterar(auth.getName(), request);

        // Encerra a sessão: o usuário entra de novo com os dados novos
        SecurityContextHolder.clearContext();
        HttpSession sessao = http.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return Map.of("message", "Dados alterados");
    }
}
