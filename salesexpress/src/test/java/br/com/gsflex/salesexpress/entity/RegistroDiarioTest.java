package br.com.gsflex.salesexpress.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistroDiarioTest {

    @Test
    void calculaValoresConformeReadme() {
        RegistroDiario r = new RegistroDiario();
        r.setQuantidadeShopee(20L);
        r.setQuantidadeML(15L);
        r.setQuantidadeAvulso(5L);
        r.setValorShopee(new BigDecimal("2.50"));
        r.setValorML(new BigDecimal("3.00"));
        r.setValorAvulso(new BigDecimal("4.00"));

        assertEquals(0, new BigDecimal("50.00").compareTo(r.totalShopee()));
        assertEquals(0, new BigDecimal("45.00").compareTo(r.totalML()));
        assertEquals(0, new BigDecimal("20.00").compareTo(r.totalAvulso()));
        assertEquals(0, new BigDecimal("115.00").compareTo(r.total()));
        assertEquals(40L, r.totalPedidos());
    }
}
