package br.com.gsflex.salesexpress.controller;

import br.com.gsflex.salesexpress.dto.request.TransportadoraRequestDto;
import br.com.gsflex.salesexpress.dto.response.TransportadoraResponseDto;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import br.com.gsflex.salesexpress.service.TransportadoraService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/v1/transportadora")
public class TransportadoraController {

    private TransportadoraService service;

    // Salvar Transportadora
    @PostMapping
    public List<TransportadoraResponseDto> salvarTransportadora (@RequestBody TransportadoraRequestDto request) {
        return service.salvarTransportadora(request);
    }

    // Buscar Transportadoras
    @GetMapping
    public List<TransportadoraResponseDto> buscarTransportadoras () {
        return service.listarTransportadoras();
    }

    @PatchMapping("/{id}")
    public List<TransportadoraResponseDto> atualizarTransportadora (@PathVariable Long id, @RequestBody TransportadoraRequestDto request) {
        return service.atualizarTransportadora(id, request);
    }

    @DeleteMapping("/{id}")
    public List<TransportadoraResponseDto> deletarTransportadora (@PathVariable Long id) {
        return service.deletarTransportadora(id);
    }
}
