package br.com.gsflex.salesexpress.controller;

import br.com.gsflex.salesexpress.service.RelatorioService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/v1/relatorio")
public class RelatorioController {

    private final RelatorioService service;

    @GetMapping("/transportadora/{id}")
    public ResponseEntity<byte[]> gerarRelatorio(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] pdf = service.gerarRelatorioTransportadora(id, inicio, fim);

        String nomeArquivo = "relatorio-transportadora-" + id + "-" + inicio + "-" + fim + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nomeArquivo).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
