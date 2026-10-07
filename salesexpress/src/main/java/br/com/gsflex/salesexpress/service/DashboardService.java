package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.dto.response.DashboardResponseDto;
import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DashboardService {

    private final RegistroDiarioRepository repository;

    public DashboardResponseDto gerarDashboard(LocalDate inicio, LocalDate fim) {

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser maior que a data final"
            );
        }

        List<RegistroDiario> registros = repository.findByDataBetween(inicio, fim);

        long quantidadeShopee = 0;
        long quantidadeML = 0;
        long quantidadeAvulso = 0;
        BigDecimal totalShopee = BigDecimal.ZERO;
        BigDecimal totalML = BigDecimal.ZERO;
        BigDecimal totalAvulso = BigDecimal.ZERO;

        Map<Long, DashboardResponseDto.PorTransportadora> porTransportadora = new LinkedHashMap<>();

        for (RegistroDiario registro : registros) {
            quantidadeShopee += registro.getQuantidadeShopee();
            quantidadeML += registro.getQuantidadeML();
            quantidadeAvulso += registro.getQuantidadeAvulso();
            totalShopee = totalShopee.add(registro.totalShopee());
            totalML = totalML.add(registro.totalML());
            totalAvulso = totalAvulso.add(registro.totalAvulso());

            Long idTransportadora = registro.getTransportadora().getId();
            porTransportadora.merge(
                    idTransportadora,
                    new DashboardResponseDto.PorTransportadora(
                            idTransportadora,
                            registro.getTransportadora().getNome(),
                            registro.totalPedidos(),
                            registro.total()),
                    (atual, novo) -> new DashboardResponseDto.PorTransportadora(
                            atual.transportadoraId(),
                            atual.nome(),
                            atual.totalPedidos() + novo.totalPedidos(),
                            atual.totalReceber().add(novo.totalReceber()))
            );
        }

        List<DashboardResponseDto.PorTransportadora> transportadoras = porTransportadora.values()
                .stream()
                .sorted(Comparator.comparing(DashboardResponseDto.PorTransportadora::totalReceber).reversed())
                .toList();

        return new DashboardResponseDto(
                new DashboardResponseDto.Periodo(inicio, fim),
                quantidadeShopee + quantidadeML + quantidadeAvulso,
                quantidadeShopee,
                quantidadeML,
                quantidadeAvulso,
                totalShopee,
                totalML,
                totalAvulso,
                totalShopee.add(totalML).add(totalAvulso),
                transportadoras
        );
    }
}
