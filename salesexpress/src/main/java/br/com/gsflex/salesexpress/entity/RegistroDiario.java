package br.com.gsflex.salesexpress.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(
        name = "registro_diario",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_registro_transportadora_data",
                        columnNames = {"transportadora_id", "data"}
                )
        }
)
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_diario")
    private Long id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transportadora_registro", nullable = false)
    private Transportadora transportadora;

    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_shopee", nullable = false)
    private Long quantidadeShopee;

    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_ml", nullable = false)
    private Long quantidadeML;

    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_avulso", nullable = false)
    private Long quantidadeAvulso;

    @Column(name = "valor_shopee", nullable = false)
    private BigDecimal valorShopee;

    @Column(name = "valor_mercado_livre", nullable = false)
    private BigDecimal valorML;

    @Column(name = "valor_avulso", nullable = false)
    private BigDecimal valorAvulso;
}
