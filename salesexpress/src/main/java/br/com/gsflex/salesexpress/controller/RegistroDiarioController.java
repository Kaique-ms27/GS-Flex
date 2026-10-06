package br.com.gsflex.salesexpress.controller;

import br.com.gsflex.salesexpress.dto.request.RegistroDiarioLoteRequestDto;
import br.com.gsflex.salesexpress.dto.response.RegistroDiarioResponseDto;
import br.com.gsflex.salesexpress.service.RegistroDiarioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("v1/registro/diario")
public class RegistroDiarioController {

    private final RegistroDiarioService service;

    @PutMapping
    public List<RegistroDiarioResponseDto> salvarDia(@RequestBody RegistroDiarioLoteRequestDto request) {
        return service.salvarRegistroDia(request);
    }
}
