package com.rionautica.loja.controller;

import com.rionautica.loja.model.Produto;
import com.rionautica.loja.repository.ProdutoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/importacao")
public class ImportacaoController {

    private final ProdutoRepository produtoRepository;

    public ImportacaoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PostMapping("/produtos")
    public ResponseEntity<ImportacaoResultado> importarProdutos(
            @RequestParam("arquivo") MultipartFile arquivo) {

        List<String> erros = new ArrayList<>();
        int importados = 0;
        int ignorados = 0;

        try (InputStream is = arquivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // começa na linha 1 pulando o cabeçalho
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String codigo = formatter.formatCellValue(row.getCell(0)).trim();
                    String nome   = formatter.formatCellValue(row.getCell(2)).trim();
                    String valorStr = formatter.formatCellValue(row.getCell(3)).trim();
                    String qtdStr   = formatter.formatCellValue(row.getCell(4)).trim();
                    String qtdMinStr = formatter.formatCellValue(row.getCell(5)).trim();

                    if (nome.isEmpty()) {
                        ignorados++;
                        continue;
                    }

                    Produto produto = new Produto();
                    produto.setCodigo(codigo.isEmpty() ? null : codigo);
                    produto.setNome(nome);
                    produto.setDescricao(null);
                    produto.setMarca(null);

                    produto.setValor(parsDouble(valorStr));
                    produto.setQuantidade(parseInt(qtdStr));
                    produto.setQuantidadeMinima(parseInt(qtdMinStr));

                    produtoRepository.save(produto);
                    importados++;

                } catch (Exception e) {
                    erros.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ImportacaoResultado(0, 0, List.of("Erro ao ler arquivo: " + e.getMessage())));
        }

        return ResponseEntity.ok(new ImportacaoResultado(importados, ignorados, erros));
    }

    private Double parsDouble(String valor) {
        if (valor == null || valor.isBlank()) return 0.0;
        // trata tanto "1.234,56" quanto "1234.56"
        String limpo = valor
                .replace("R$", "")
                .replace(" ", "")
                .trim();

        if (limpo.contains(",") && limpo.contains(".")) {
            // formato brasileiro: 1.234,56
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (limpo.contains(",")) {
            // só vírgula: 1234,56
            limpo = limpo.replace(",", ".");
        }

        try {
            return Double.parseDouble(limpo);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Integer parseInt(String valor) {
        if (valor == null || valor.isBlank()) return 0;
        try {
            return (int) Double.parseDouble(valor.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    record ImportacaoResultado(int importados, int ignorados, List<String> erros) {}
}