package br.com.gsflex.salesexpress.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroDiarioResponseDto(Long id, LocalDate data, Long transportadora,
                                        Long quantidaeShopee, Long quantidadeML,
                                        Long quantidadeAvulso, BigDecimal valorShopee,
                                        BigDecimal valorML, BigDecimal valorAvulso) {
}
