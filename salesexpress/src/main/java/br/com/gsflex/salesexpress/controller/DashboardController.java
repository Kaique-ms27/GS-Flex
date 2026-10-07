package br.com.gsflex.salesexpress.controller;

import br.com.gsflex.salesexpress.dto.response.DashboardResponseDto;
import br.com.gsflex.salesexpress.service.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public DashboardResponseDto gerarDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return service.gerarDashboard(inicio, fim);
    }
}
