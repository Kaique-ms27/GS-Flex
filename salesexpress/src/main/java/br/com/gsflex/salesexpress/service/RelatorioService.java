package br.com.gsflex.salesexpress.service;

import br.com.gsflex.salesexpress.entity.RegistroDiario;
import br.com.gsflex.salesexpress.entity.Transportadora;
import br.com.gsflex.salesexpress.exception.ResourceNotFoundException;
import br.com.gsflex.salesexpress.repository.RegistroDiarioRepository;
import br.com.gsflex.salesexpress.repository.TransportadoraRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class RelatorioService {

    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color COR_CABECALHO = new Color(220, 220, 220);

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

        for (RegistroDiario r : registros) {
            qtdShopee += r.getQuantidadeShopee();
            qtdML += r.getQuantidadeML();
            qtdAvulso += r.getQuantidadeAvulso();
            valShopee = valShopee.add(r.totalShopee());
            valML = valML.add(r.totalML());
            valAvulso = valAvulso.add(r.totalAvulso());
        }
        long totalPedidos = qtdShopee + qtdML + qtdAvulso;
        BigDecimal totalReceber = valShopee.add(valML).add(valAvulso);

        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            Paragraph cabecalho = new Paragraph("Relatório de Pedidos", titulo);
            cabecalho.setSpacingAfter(10);
            documento.add(cabecalho);
            documento.add(new Paragraph("Transportadora: " + transportadora.getNome(), negrito));
            Paragraph periodo = new Paragraph(
                    "Período: " + inicio.format(FORMATO_DATA) + " até " + fim.format(FORMATO_DATA), normal);
            periodo.setSpacingAfter(15);
            documento.add(periodo);

            // Registros diários
            PdfPTable diario = new PdfPTable(new float[]{2f, 2f, 2f, 2f, 3f});
            diario.setWidthPercentage(100);
            for (String coluna : new String[]{"DATA", "SHOPEE", "ML", "AVULSO", "TOTAL"}) {
                diario.addCell(celula(coluna, negrito, Element.ALIGN_CENTER, COR_CABECALHO));
            }
            for (RegistroDiario r : registros) {
                diario.addCell(celula(r.getData().format(FORMATO_DIA), normal, Element.ALIGN_CENTER, null));
                diario.addCell(celula(String.valueOf(r.getQuantidadeShopee()), normal, Element.ALIGN_CENTER, null));
                diario.addCell(celula(String.valueOf(r.getQuantidadeML()), normal, Element.ALIGN_CENTER, null));
                diario.addCell(celula(String.valueOf(r.getQuantidadeAvulso()), normal, Element.ALIGN_CENTER, null));
                diario.addCell(celula(moeda(r.total()), normal, Element.ALIGN_RIGHT, null));
            }
            diario.setSpacingAfter(20);
            documento.add(diario);

            // Totais
            PdfPTable totais = new PdfPTable(new float[]{4f, 2f, 3f});
            totais.setWidthPercentage(100);
            for (String coluna : new String[]{"MODALIDADE", "QUANTIDADE", "VALOR"}) {
                totais.addCell(celula(coluna, negrito, Element.ALIGN_CENTER, COR_CABECALHO));
            }
            linhaTotal(totais, "TOTAL SHOPEE", qtdShopee, valShopee, normal);
            linhaTotal(totais, "TOTAL MERCADO LIVRE", qtdML, valML, normal);
            linhaTotal(totais, "TOTAL AVULSO", qtdAvulso, valAvulso, normal);
            totais.addCell(celula("TOTAL DE PEDIDOS", negrito, Element.ALIGN_LEFT, null));
            totais.addCell(celula(String.valueOf(totalPedidos), negrito, Element.ALIGN_CENTER, null));
            totais.addCell(celula("", negrito, Element.ALIGN_RIGHT, null));
            totais.addCell(celula("TOTAL A RECEBER", negrito, Element.ALIGN_LEFT, COR_CABECALHO));
            totais.addCell(celula("", negrito, Element.ALIGN_CENTER, COR_CABECALHO));
            totais.addCell(celula(moeda(totalReceber), negrito, Element.ALIGN_RIGHT, COR_CABECALHO));
            documento.add(totais);

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

    private void linhaTotal(PdfPTable tabela, String nome, long quantidade, BigDecimal valor, Font fonte) {
        tabela.addCell(celula(nome, fonte, Element.ALIGN_LEFT, null));
        tabela.addCell(celula(String.valueOf(quantidade), fonte, Element.ALIGN_CENTER, null));
        tabela.addCell(celula(moeda(valor), fonte, Element.ALIGN_RIGHT, null));
    }

    private PdfPCell celula(String texto, Font fonte, int alinhamento, Color fundo) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, fonte));
        celula.setHorizontalAlignment(alinhamento);
        celula.setPadding(5);
        if (fundo != null) {
            celula.setBackgroundColor(fundo);
        }
        return celula;
    }

    private String moeda(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));
        return "R$ " + formato.format(valor);
    }
}
