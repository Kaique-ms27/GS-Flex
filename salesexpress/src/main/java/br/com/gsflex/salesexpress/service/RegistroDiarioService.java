package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.dto.request.RegistroDiarioLoteRequestDto;
import br.com.gsflex.salesexpress.dto.request.RegistroDiarioRequestDto;
import br.com.gsflex.salesexpress.dto.response.RegistroDiarioResponseDto;
import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.entity.Transportadora;
import br.com.gsflex.salesexpress.exception.BusinessException;
import br.com.gsflex.salesexpress.exception.ResourceNotFoundException;
import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RegistroDiarioService {

    private final RegistroDiarioRepository rRepository;
    private final TransportadoraRepository tRepository;

    @Transactional
    public List<RegistroDiarioResponseDto> salvarRegistroDia(
            RegistroDiarioLoteRequestDto request
    ) {

        Set<Long> idsVistos = new HashSet<>();
        for (RegistroDiarioRequestDto dto : request.registros()) {
            if(!idsVistos.add(dto.transportadoraId())) {
                Transportadora transportadora = tRepository.findById(dto.transportadoraId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Transportadora não encontrada com ID: " + dto.transportadoraId()));
                throw new BusinessException(
                        "A transportadora " + transportadora.getNome() + " está duplicada na requisição");
            }
        }
        return request.registros()
                .stream()
                .map(registroRequest -> {
                    Transportadora transportadora =
                            tRepository.findById(registroRequest.transportadoraId())
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException("Transportadora não encontrada"));


                    RegistroDiario registro =
                            rRepository.findByDataAndTransportadora(request.data(),transportadora)
                                    .orElseGet(RegistroDiario::new);

                    if (registroRequest.quantidadeShopee() < 0) {
                        throw new BusinessException("Quantidade de Shopee inválida");
                    } else if (registroRequest.quantidadeAvulso() < 0) {
                        throw new BusinessException("Quantidade de Avulso inválida");
                    } else if (registroRequest.quantidadeML() < 0) {
                        throw new BusinessException("Quantidade de ML inválida");
                    }

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
