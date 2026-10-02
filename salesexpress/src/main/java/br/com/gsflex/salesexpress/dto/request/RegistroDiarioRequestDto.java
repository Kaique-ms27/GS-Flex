package br.com.gsflex.salesexpress.dto.request;

import br.com.gsflex.salesexpress.entity.Transportadora;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroDiarioRequestDto(LocalDate data, Transportadora transportadora,
                                       Long quantidaeShopee, Long quantidadeML,
                                       Long quantidadeAvulso, BigDecimal valorShopee,
                                       BigDecimal valorML, BigDecimal valorAvulso) {
}
