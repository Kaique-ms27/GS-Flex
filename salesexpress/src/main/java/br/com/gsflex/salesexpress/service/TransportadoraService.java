package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.dto.request.TransportadoraRequestDto;
import br.com.gsflex.salesexpress.dto.response.TransportadoraResponseDto;
import br.com.gsflex.salesexpress.entity.Transportadora;
import br.com.gsflex.salesexpress.exception.BusinessException;
import br.com.gsflex.salesexpress.exception.ResourceNotFoundException;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransportadoraService {

    private final TransportadoraRepository repository;

    // Create
    public List<TransportadoraResponseDto> salvarTransportadora(TransportadoraRequestDto request) {

        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma transportadora com este nome");
        }

        Transportadora transportadora = new Transportadora();

        transportadora.setNome(request.nome());
        transportadora.setValorShopee(request.valorShopee());
        transportadora.setValorML(request.valorML());
        transportadora.setValorAvulso(request.valorAvulso());

        repository.save(transportadora);


        List<Transportadora> transportadoras = repository.findAll();

        return TransportadoraResponseDto.toList(transportadoras);
    }

    // Read
    public List<TransportadoraResponseDto> listarTransportadoras() {
        List<Transportadora> transportadoras = repository.findAll();

        return TransportadoraResponseDto.toList(transportadoras);
    }

    // Update
    public List<TransportadoraResponseDto> atualizarTransportadora(Long id, TransportadoraRequestDto request) {

        Transportadora transportadoraAtualizada = repository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Transportadora não encontrada"));

        if (request.nome() != null) {
            transportadoraAtualizada.setNome(request.nome());
        }

        if (request.valorShopee() != null) {
            transportadoraAtualizada.setValorShopee(request.valorShopee());
        }

        if (request.valorML() != null) {
            transportadoraAtualizada.setValorML(request.valorML());
        }

        if (request.valorAvulso() != null) {
            transportadoraAtualizada.setValorAvulso(request.valorAvulso());
        }

        repository.save(transportadoraAtualizada);


        List<Transportadora> transportadoras = repository.findAll();
        return TransportadoraResponseDto.toList(transportadoras);
    }

    // Delete
    public List<TransportadoraResponseDto> deletarTransportadora(Long id) {
        Transportadora transportadora = repository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Transportadora não encontrada"));

        repository.delete(transportadora);


        List<Transportadora> transportadoras = repository.findAll();
        return TransportadoraResponseDto.toList(transportadoras);
    }
}
