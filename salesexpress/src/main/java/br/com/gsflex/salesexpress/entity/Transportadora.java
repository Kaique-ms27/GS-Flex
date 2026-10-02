package br.com.gsflex.salesexpress.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transportadora")
public class Transportadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transportadora", nullable = false)
    private Long id;

    @Column(name = "nome_transportadora", nullable = false)
    private String nome;

    @Column(name = "valor_shopee", nullable = false)
    private BigDecimal valorShopee;

    @Column(name = "valor_mercado_livre", nullable = false)
    private BigDecimal valorML;

    @Column(name = "valor_avulso", nullable = false)
    private BigDecimal valorAvulso;

}
