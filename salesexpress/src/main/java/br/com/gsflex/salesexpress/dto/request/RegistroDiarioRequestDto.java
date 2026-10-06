package br.com.gsflex.salesexpress.dto.request;

import java.time.LocalDate;

public record RegistroDiarioRequestDto(LocalDate data, Long transportadoraId,
                                       Long quantidadeShopee, Long quantidadeML,
                                       Long quantidadeAvulso) {
}
