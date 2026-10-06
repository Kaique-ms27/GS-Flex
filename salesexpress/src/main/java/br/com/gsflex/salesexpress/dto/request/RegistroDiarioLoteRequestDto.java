package br.com.gsflex.salesexpress.dto.request;

import java.time.LocalDate;
import java.util.List;

public record RegistroDiarioLoteRequestDto(LocalDate data,
                                           List<RegistroDiarioRequestDto> registros) {
}
