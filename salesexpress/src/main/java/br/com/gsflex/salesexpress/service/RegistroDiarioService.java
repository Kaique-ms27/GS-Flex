package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.dto.request.RegistroDiarioLoteRequestDto;
import br.com.gsflex.salesexpress.dto.response.RegistroDiarioResponseDto;
import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.entity.Transportadora;
import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RegistroDiarioService {

    private final RegistroDiarioRepository rRepository;
    private final TransportadoraRepository tRepository;

    @Transactional
    public List<RegistroDiarioResponseDto> salvarRegistroDia(
            RegistroDiarioLoteRequestDto request
    ) {
        System.out.println(request);
        return request.registros()
                .stream()
                .map(registroRequest -> {
                    Transportadora transportadora =
                            tRepository.findById(registroRequest.transportadoraId())
                                    .orElseThrow(() ->
                                            new RuntimeException("Transportadora não encontrada"));


                    RegistroDiario registro =
                            rRepository.findByDataAndTransportadora(request.data(),transportadora)
                                    .orElseGet(RegistroDiario::new);

                    boolean novoRegistro = registro.getId() == null;

                    registro.setData(request.data());
                    registro.setTransportadora(transportadora);
                    registro.setQuantidadeShopee(registroRequest.quantidadeShopee());
                    registro.setQuantidadeML(registroRequest.quantidadeML());
                    registro.setQuantidadeAvulso(registroRequest.quantidadeAvulso());

                    if (novoRegistro) {
                        registro.setValorShopee(transportadora.getValorShopee());
                        registro.setValorML(transportadora.getValorML());
                        registro.setValorAvulso(transportadora.getValorAvulso());
                    }

                    return rRepository.save(registro);
                })
                .map(this::toResponse)
                .toList();
    }

    private RegistroDiarioResponseDto toResponse(RegistroDiario registo) {
        return new RegistroDiarioResponseDto(
                registo.getId(),
                registo.getData(),
                registo.getTransportadora().getId(),
                registo.getQuantidadeShopee(),
                registo.getQuantidadeML(),
                registo.getQuantidadeAvulso(),
                registo.getValorShopee(),
                registo.getValorML(),
                registo.getValorAvulso()
        );
    }

    public List<RegistroDiarioResponseDto> buscarPorData(
            LocalDate dataInicial, LocalDate dataFinal) {

        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser maior que a data final"
            );
        }

        return rRepository.findByDataBetween(dataInicial, dataFinal)
                .stream()
                .map(this::toResponse)
                .toList();

    }
}
