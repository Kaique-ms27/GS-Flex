package br.com.gsflex.salesexpress.repository;

import br.com.gsflex.salesexpress.entity.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportadoraRepository extends JpaRepository<Transportadora, Long> {

    public boolean existsByNomeIgnoreCase (String nome);
}
