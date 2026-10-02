package br.com.gsflex.salesexpress.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransportadoraRequestDto(@NotBlank
                                       String nome,

                                       @DecimalMin(value = "0.00")
                                       @NotNull
                                       BigDecimal valorShopee,

                                       @DecimalMin(value = "0.00")
                                       @NotNull
                                       BigDecimal valorML,

                                       @DecimalMin(value = "0.00")
                                       @NotNull
                                       BigDecimal valorAvulso) {
}
