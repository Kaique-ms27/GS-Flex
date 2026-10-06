package br.com.gsflex.salesexpress.repository;

import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.entity.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroDiarioRepository extends JpaRepository<RegistroDiario, Long> {

    Optional<RegistroDiario> findByDataAndTransportadora(
            LocalDate data,
            Transportadora transportadora
    );

    List<RegistroDiario> findByDataBetween(
            LocalDate dataInicial,
            LocalDate dataFinal
    );
}
