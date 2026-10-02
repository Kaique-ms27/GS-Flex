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
    @Column(name = "id_transportadora")
    private Long id;

    @Column(name = "nome_transportadora")
    private String nome;

    @Column(name = "valor_shopee")
    private BigDecimal valorShopee;

    @Column(name = "valor_mercado_livre")
    private BigDecimal valorMercadoLivre;

    @Column(name = "valor_avulso")
    private BigDecimal valorAvulso;

}
