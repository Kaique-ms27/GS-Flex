package br.com.gsflex.salesexpress.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "registro_diario")
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro_diario")
    private Long id;

    @Column(name = "data")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "transportadora_registro")
    private Transportadora transportadora;

    @Column(name = "quantidade_shopee")
    private Long quantidadeShopee;

    @Column(name = "quantidade_ml")
    private Long quantidadeML;

    @Column(name = "quantidade_avulso")
    private Long quantidadeAvulso;

    @Column(name = "valor_shopee")
    private BigDecimal valorShopee;

    @Column(name = "valor_mercado_livre")
    private BigDecimal valorML;

    @Column(name = "valor_avulso")
    private BigDecimal valorAvulso;
}
