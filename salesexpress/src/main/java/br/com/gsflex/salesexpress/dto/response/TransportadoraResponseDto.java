package br.com.gsflex.salesexpress.dto.response;

import br.com.gsflex.salesexpress.entity.Transportadora;

import java.math.BigDecimal;
import java.util.List;

public record TransportadoraResponseDto(Long id, String nome, BigDecimal valorShopee,
                                        BigDecimal valorML, BigDecimal valorAvulso) {

    public TransportadoraResponseDto (Transportadora transportadora) {
        this(transportadora.getId(), transportadora.getNome(), transportadora.getValorShopee(),
                transportadora.getValorML(), transportadora.getValorAvulso()
        );
    }

    public static List<TransportadoraResponseDto> toList(List<Transportadora> transportadoras) {
        if (transportadoras == null) {
            return List.of();
        }
        return transportadoras.stream()
                .map(TransportadoraResponseDto::new)
                .toList();
    }
}
