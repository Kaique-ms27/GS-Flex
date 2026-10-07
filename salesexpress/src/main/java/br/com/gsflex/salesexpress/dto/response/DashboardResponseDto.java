package br.com.gsflex.salesexpress.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponseDto(Periodo periodo,
                                   Long totalPedidos,
                                   Long quantidadeShopee,
                                   Long quantidadeML,
                                   Long quantidadeAvulso,
                                   BigDecimal totalShopee,
                                   BigDecimal totalML,
                                   BigDecimal totalAvulso,
                                   BigDecimal totalReceber,
                                   List<PorTransportadora> transportadoras) {

    public record Periodo(LocalDate inicio, LocalDate fim) {
    }

    public record PorTransportadora(Long transportadoraId, String nome,
                                    Long totalPedidos, BigDecimal totalReceber) {
    }
}
