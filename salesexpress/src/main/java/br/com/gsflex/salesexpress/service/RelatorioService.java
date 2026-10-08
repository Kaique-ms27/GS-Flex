package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.entity.Transportadora;
import br.com.gsflex.salesexpress.exception.ResourceNotFoundException;
import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class RelatorioService {

    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Paleta do sistema (mesma do index.html)
    private static final Color PRETO = new Color(10, 10, 10);
    private static final Color VERMELHO = new Color(208, 16, 26);
    private static final Color CINZA = new Color(244, 244, 244);
    private static final Color BORDA = new Color(224, 224, 224);
    private static final Color TEXTO_SECUNDARIO = new Color(85, 85, 85);

    private final RegistroDiarioRepository rRepository;
    private final TransportadoraRepository tRepository;

    public byte[] gerarRelatorioTransportadora(Long transportadoraId, LocalDate inicio, LocalDate fim) {

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser maior que a data final"
            );
        }

        Transportadora transportadora = tRepository.findById(transportadoraId)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora não encontrada"));

        List<RegistroDiario> registros =
                rRepository.findByTransportadoraIdAndDataBetweenOrderByData(transportadoraId, inicio, fim);

        long qtdShopee = 0, qtdML = 0, qtdAvulso = 0;
        BigDecimal valShopee = BigDecimal.ZERO, valML = BigDecimal.ZERO, valAvulso = BigDecimal.ZERO;
        boolean valoresDiferentes = false;

        for (RegistroDiario r : registros) {
            qtdShopee += r.getQuantidadeShopee();
            qtdML += r.getQuantidadeML();
            qtdAvulso += r.getQuantidadeAvulso();
            valShopee = valShopee.add(r.totalShopee());
            valML = valML.add(r.totalML());
            valAvulso = valAvulso.add(r.totalAvulso());

            if (r.getValorShopee().compareTo(transportadora.getValorShopee()) != 0
                    || r.getValorML().compareTo(transportadora.getValorML()) != 0
                    || r.getValorAvulso().compareTo(transportadora.getValorAvulso()) != 0) {
                valoresDiferentes = true;
            }
        }
        long totalPedidos = qtdShopee + qtdML + qtdAvulso;
        BigDecimal totalReceber = valShopee.add(valML).add(valAvulso);

        // Fontes
        Font brancoTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.NORMAL, Color.WHITE);
        Font vermelhoTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.NORMAL, VERMELHO);
        Font brancoSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.NORMAL, Color.WHITE);
        Font cabecalhoTabela = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, Color.WHITE);
        Font rotulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.NORMAL, VERMELHO);
        Font valorGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, PRETO);
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, PRETO);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, PRETO);
        Font pequeno = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, TEXTO_SECUNDARIO);

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            // Faixa de título
            PdfPTable banner = new PdfPTable(new float[]{1f, 1f});
            banner.setWidthPercentage(100);
            Phrase marca = new Phrase();
            marca.add(new Chunk("GS ", brancoTitulo));
            marca.add(new Chunk("FLEX", vermelhoTitulo));
            banner.addCell(celulaBanner(marca, Element.ALIGN_LEFT));
            banner.addCell(celulaBanner(new Phrase("RELATÓRIO DE PEDIDOS", brancoSub), Element.ALIGN_RIGHT));
            banner.setSpacingAfter(14);
            documento.add(banner);

            // Informações da transportadora
            PdfPTable info = new PdfPTable(new float[]{3f, 2f});
            info.setWidthPercentage(100);
            info.addCell(bloco("TRANSPORTADORA", transportadora.getNome(), rotulo, valorGrande));
            info.addCell(bloco("PERÍODO",
                    inicio.format(FORMATO_DATA) + " até " + fim.format(FORMATO_DATA), rotulo, negrito));
            info.setSpacingAfter(6);
            documento.add(info);

            PdfPTable valores = new PdfPTable(new float[]{1f, 1f, 1f});
            valores.setWidthPercentage(100);
            valores.addCell(bloco("VALOR POR PEDIDO · SHOPEE", moeda(transportadora.getValorShopee()), rotulo, valorGrande));
            valores.addCell(bloco("VALOR POR PEDIDO · MERCADO LIVRE", moeda(transportadora.getValorML()), rotulo, valorGrande));
            valores.addCell(bloco("VALOR POR PEDIDO · AVULSO", moeda(transportadora.getValorAvulso()), rotulo, valorGrande));
            valores.setSpacingAfter(valoresDiferentes ? 4 : 18);
            documento.add(valores);

            if (valoresDiferentes) {
                Paragraph nota = new Paragraph(
                        "Alguns lançamentos do período usam valores anteriores aos atuais. "
                                + "Os totais abaixo consideram o valor vigente na data de cada lançamento.", pequeno);
                nota.setSpacingAfter(18);
                documento.add(nota);
            }

            // Registros diários
            PdfPTable diario = new PdfPTable(new float[]{2f, 2f, 2f, 2f, 3f});
            diario.setWidthPercentage(100);
            diario.setHeaderRows(1);
            for (String coluna : new String[]{"DATA", "SHOPEE", "ML", "AVULSO", "TOTAL"}) {
                diario.addCell(celula(coluna, cabecalhoTabela, Element.ALIGN_CENTER, PRETO));
            }
            if (registros.isEmpty()) {
                PdfPCell vazio = celula("Nenhum lançamento no período", pequeno, Element.ALIGN_CENTER, null);
                vazio.setColspan(5);
                vazio.setPadding(12);
                diario.addCell(vazio);
            }
            int linha = 0;
            for (RegistroDiario r : registros) {
                Color fundo = (linha++ % 2 == 1) ? CINZA : null;
                diario.addCell(celula(r.getData().format(FORMATO_DIA), normal, Element.ALIGN_CENTER, fundo));
                diario.addCell(celula(String.valueOf(r.getQuantidadeShopee()), normal, Element.ALIGN_CENTER, fundo));
                diario.addCell(celula(String.valueOf(r.getQuantidadeML()), normal, Element.ALIGN_CENTER, fundo));
                diario.addCell(celula(String.valueOf(r.getQuantidadeAvulso()), normal, Element.ALIGN_CENTER, fundo));
                diario.addCell(celula(moeda(r.total()), negrito, Element.ALIGN_RIGHT, fundo));
            }
            diario.setSpacingAfter(20);
            documento.add(diario);

            // Totais
            PdfPTable totais = new PdfPTable(new float[]{4f, 2f, 3f});
            totais.setWidthPercentage(100);
            totais.setKeepTogether(true);
            for (String coluna : new String[]{"MODALIDADE", "QUANTIDADE", "VALOR"}) {
                totais.addCell(celula(coluna, cabecalhoTabela, Element.ALIGN_CENTER, PRETO));
            }
            linhaTotal(totais, "TOTAL SHOPEE", qtdShopee, valShopee, normal, null);
            linhaTotal(totais, "TOTAL MERCADO LIVRE", qtdML, valML, normal, CINZA);
            linhaTotal(totais, "TOTAL AVULSO", qtdAvulso, valAvulso, normal, null);

            totais.addCell(celula("TOTAL DE PEDIDOS", negrito, Element.ALIGN_LEFT, CINZA));
            totais.addCell(celula(String.valueOf(totalPedidos), negrito, Element.ALIGN_CENTER, CINZA));
            totais.addCell(celula("", negrito, Element.ALIGN_RIGHT, CINZA));

            Font brancoNegrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.NORMAL, Color.WHITE);
            totais.addCell(celula("TOTAL A RECEBER", brancoNegrito, Element.ALIGN_LEFT, VERMELHO));
            totais.addCell(celula("", brancoNegrito, Element.ALIGN_CENTER, VERMELHO));
            totais.addCell(celula(moeda(totalReceber), brancoNegrito, Element.ALIGN_RIGHT, VERMELHO));
            documento.add(totais);

            Paragraph rodape = new Paragraph(
                    "Gerado em " + LocalDateTime.now().format(FORMATO_DATA_HORA), pequeno);
            rodape.setAlignment(Element.ALIGN_RIGHT);
            rodape.setSpacingBefore(12);
            documento.add(rodape);

            documento.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar o relatório em PDF", e);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }

        return saida.toByteArray();
    }

    private void linhaTotal(PdfPTable tabela, String nome, long quantidade, BigDecimal valor,
                            Font fonte, Color fundo) {
        tabela.addCell(celula(nome, fonte, Element.ALIGN_LEFT, fundo));
        tabela.addCell(celula(String.valueOf(quantidade), fonte, Element.ALIGN_CENTER, fundo));
        tabela.addCell(celula(moeda(valor), fonte, Element.ALIGN_RIGHT, fundo));
    }

    private PdfPCell celula(String texto, Font fonte, int alinhamento, Color fundo) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, fonte));
        celula.setHorizontalAlignment(alinhamento);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setPadding(6);
        celula.setBorderColor(BORDA);
        if (fundo != null) {
            celula.setBackgroundColor(fundo);
            if (fundo == PRETO || fundo == VERMELHO) {
                celula.setBorderColor(fundo);
            }
        }
        return celula;
    }

    /** Célula da faixa preta do topo, com linha vermelha embaixo. */
    private PdfPCell celulaBanner(Phrase conteudo, int alinhamento) {
        PdfPCell celula = new PdfPCell(conteudo);
        celula.setBackgroundColor(PRETO);
        celula.setHorizontalAlignment(alinhamento);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setPadding(12);
        celula.setBorder(Rectangle.BOTTOM);
        celula.setBorderWidthBottom(4);
        celula.setBorderColorBottom(VERMELHO);
        return celula;
    }

    /** Bloco cinza com rótulo vermelho pequeno, valor embaixo e borda superior vermelha. */
    private PdfPCell bloco(String rotuloTexto, String valor, Font fonteRotulo, Font fonteValor) {
        Phrase frase = new Phrase();
        frase.add(new Chunk(rotuloTexto + "\n", fonteRotulo));
        frase.add(new Chunk(valor, fonteValor));
        PdfPCell celula = new PdfPCell(frase);
        celula.setBackgroundColor(CINZA);
        celula.setPadding(9);
        celula.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT);
        celula.setBorderWidthTop(3);
        celula.setBorderColorTop(VERMELHO);
        celula.setBorderWidthLeft(3);
        celula.setBorderColorLeft(Color.WHITE);
        celula.setBorderWidthRight(3);
        celula.setBorderColorRight(Color.WHITE);
        return celula;
    }

    private String moeda(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));
        return "R$ " + formato.format(valor);
    }
}
