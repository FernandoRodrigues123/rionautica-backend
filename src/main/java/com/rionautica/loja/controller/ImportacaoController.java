package com.rionautica.loja.controller;

import com.rionautica.loja.model.ClientePessoaFisica;
import com.rionautica.loja.model.ClientePessoaJuridica;
import com.rionautica.loja.model.Produto;
import com.rionautica.loja.repository.ClientePessoaFisicaRepository;
import com.rionautica.loja.repository.ClientePessoaJuridicaRepository;
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

    private final ClientePessoaJuridicaRepository clientePessoaJuridicaRepository;
    private final ClientePessoaFisicaRepository clientePessoaFisicaRepository;
    private final ProdutoRepository produtoRepository;

    public ImportacaoController(ClientePessoaJuridicaRepository clientePessoaJuridicaRepository, ProdutoRepository produtoRepository, ClientePessoaFisicaRepository clientePessoaFisicaRepository) {
        this.produtoRepository = produtoRepository;
        this.clientePessoaFisicaRepository =clientePessoaFisicaRepository;
        this.clientePessoaJuridicaRepository = clientePessoaJuridicaRepository;
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

    @PostMapping("/clientes")
    public ResponseEntity<ImportacaoResultado> importarClientes(
            @RequestParam("arquivo") MultipartFile arquivo) {

        List<String> erros = new ArrayList<>();
        int importados = 0;
        int ignorados = 0;

        try (InputStream is = arquivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String nome          = formatter.formatCellValue(row.getCell(0)).trim();
                    String razaoSocial   = formatter.formatCellValue(row.getCell(1)).trim();
                    String cnpj          = formatter.formatCellValue(row.getCell(2)).trim();
                    String cpf           = formatter.formatCellValue(row.getCell(3)).trim();
                    String email         = formatter.formatCellValue(row.getCell(10)).trim();
                    String telefone      = formatter.formatCellValue(row.getCell(11)).trim();
                    String cep           = formatter.formatCellValue(row.getCell(13)).trim();
                    String estado        = formatter.formatCellValue(row.getCell(14)).trim();
                    String cidade        = formatter.formatCellValue(row.getCell(15)).trim();
                    String endereco      = formatter.formatCellValue(row.getCell(16)).trim();
                    String numero        = formatter.formatCellValue(row.getCell(17)).trim();

                    // determina tipo
                    boolean isPJ = !cnpj.isEmpty();
                    boolean isPF = !cpf.isEmpty();

                    if (!isPJ && !isPF) {
                        ignorados++;
                        continue;
                    }

                    // normaliza campos obrigatórios
                    String nomeCliente = isPJ && !razaoSocial.isEmpty() ? razaoSocial : nome;
                    if (nomeCliente.isEmpty()) { ignorados++; continue; }

                    // email fallback
                    if (email.isEmpty()) email = "sem-email-" + i + "@importacao.com";

                    // campos de endereço com fallback
                    if (cep.isEmpty()) cep = "00000000";
                    if (estado.isEmpty()) estado = "PR";
                    if (cidade.isEmpty()) cidade = "Não informado";
                    if (endereco.isEmpty()) endereco = "Não informado";
                    if (numero.isEmpty()) numero = "S/N";
                    if (telefone.isEmpty()) telefone = "Não informado";

                    // limpa formatação de CPF/CNPJ
                    cpf   = cpf.replaceAll("[^0-9]", "");
                    cnpj  = cnpj.replaceAll("[^0-9]", "");
                    cep   = cep.replaceAll("[^0-9]", "");
                    estado = estado.length() > 2 ? estado.substring(0, 2).toUpperCase() : estado.toUpperCase();

                    if (isPF) {
                        ClientePessoaFisica pf = new ClientePessoaFisica();
                        pf.setNome(nomeCliente);
                        pf.setEmail(email);
                        pf.setCelular(telefone);
                        pf.setCep(cep);
                        pf.setEstado(estado);
                        pf.setCidade(cidade);
                        pf.setEndereco(endereco);
                        pf.setNumero(numero);
                        pf.setCpf(cpf.isEmpty() ? "00000000000" : cpf);
                        clientePessoaFisicaRepository.save(pf);
                    } else {
                        ClientePessoaJuridica pj = new ClientePessoaJuridica();
                        pj.setNome(nomeCliente);
                        pj.setEmail(email);
                        pj.setCelular(telefone);
                        pj.setCep(cep);
                        pj.setEstado(estado);
                        pj.setCidade(cidade);
                        pj.setEndereco(endereco);
                        pj.setNumero(numero);
                        pj.setCnpj(cnpj.isEmpty() ? "00000000000000" : cnpj);
                        clientePessoaJuridicaRepository.save(pj);
                    }

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
}