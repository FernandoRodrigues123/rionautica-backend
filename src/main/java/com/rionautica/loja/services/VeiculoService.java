package com.rionautica.loja.services;

import com.rionautica.loja.model.*;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    public VeiculoService(VeiculoRepository veiculoRepository,
                          ClienteRepository clienteRepository) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    public Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + id));
    }

    public List<Veiculo> listarPorCliente(Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }

    public List<Veiculo> listarComRevisaoPendente() {
        return veiculoRepository.findByRevisaoEmDiaFalse();
    }

    public Veiculo salvar(Long clienteId, Veiculo veiculo) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clienteId));
        veiculo.setCliente(cliente);
        return veiculoRepository.save(veiculo);
    }

    public Veiculo atualizar(Long id, Veiculo dados) {
        Veiculo veiculo = buscarPorId(id);
        veiculo.setNome(dados.getNome());
        veiculo.setDescricao(dados.getDescricao());
        veiculo.setMarca(dados.getMarca());
        veiculo.setModelo(dados.getModelo());
        veiculo.setAno(dados.getAno());
        veiculo.setChassi(dados.getChassi());
        veiculo.setHorasAtuais(dados.getHorasAtuais());
        veiculo.setIntervaloHoras(dados.getIntervaloHoras());
        veiculo.setIntervaloMeses(dados.getIntervaloMeses());
        return veiculoRepository.save(veiculo);
    }

    public void deletar(Long id) {
        veiculoRepository.delete(buscarPorId(id));
    }

    // -------------------------
    // Chamado pela NotaService ao detectar serviço de revisão
    // -------------------------
    @Transactional
    public void processarServicosDaNota(Nota nota) {
        for (ServicoNota servicoNota : nota.getServicos()) {

            // só processa se o serviço tiver um veículo associado
            if (servicoNota.getVeiculo() == null) continue;

            Veiculo veiculo = buscarPorId(servicoNota.getVeiculo().getId());

            // registra o serviço no histórico do veículo
            ServicoVeiculo sv = new ServicoVeiculo();
            sv.setVeiculo(veiculo);
            sv.setNota(nota);
            sv.setServico(servicoNota.getServico());
            sv.setMecanico(servicoNota.getMecanico());
            sv.setDescricao(servicoNota.getDescricao());
            sv.setDataRealizacao(LocalDateTime.now());
            sv.setHorasNaRealizacao(servicoNota.getHorasVeiculo());
            veiculo.getServicos().add(sv);

            // se for revisão, registra também no histórico de revisões
            if (Boolean.TRUE.equals(servicoNota.getServico().getRevisao())) {
                Revisao revisao = new Revisao();
                revisao.setVeiculo(veiculo);
                revisao.setNota(nota);
                revisao.setMecanico(servicoNota.getMecanico());
                revisao.setDataRealizacao(LocalDateTime.now());
                revisao.setHorasNaRevisao(servicoNota.getHorasVeiculo());
                revisao.setObservacao(servicoNota.getDescricao());

                // calcula próxima revisão
                if (veiculo.getIntervaloMeses() != null) {
                    revisao.setProximaRevisaoData(
                            LocalDateTime.now().plusMonths(veiculo.getIntervaloMeses())
                    );
                }
                if (veiculo.getIntervaloHoras() != null && servicoNota.getHorasVeiculo() != null) {
                    revisao.setProximaRevisaoHoras(
                            servicoNota.getHorasVeiculo() + veiculo.getIntervaloHoras()
                    );
                }

                veiculo.getRevisoes().add(revisao);
                veiculo.setRevisaoEmDia(true); // acabou de revisar
            }

            // atualiza horas do veículo
            if (servicoNota.getHorasVeiculo() != null) {
                veiculo.setHorasAtuais(servicoNota.getHorasVeiculo());
            }

            veiculoRepository.save(veiculo);
        }

        // recalcula revisaoEmDia de todos os veículos da nota
        recalcularRevisaoEmDia();
    }

    // verifica todos os veículos e atualiza revisaoEmDia
    @Transactional
    public void recalcularRevisaoEmDia() {
        LocalDateTime agora = LocalDateTime.now();

        for (Veiculo veiculo : veiculoRepository.findAll()) {
            if (veiculo.getRevisoes().isEmpty()) continue;

            // pega a revisão mais recente
            Revisao ultima = veiculo.getRevisoes().stream()
                    .max(Comparator.comparing(Revisao::getDataRealizacao))
                    .orElse(null);

            if (ultima == null) continue;

            boolean atrasadaPorData = ultima.getProximaRevisaoData() != null
                    && ultima.getProximaRevisaoData().isBefore(agora);

            boolean atrasadaPorHoras = ultima.getProximaRevisaoHoras() != null
                    && veiculo.getHorasAtuais() != null
                    && veiculo.getHorasAtuais() >= ultima.getProximaRevisaoHoras();

            veiculo.setRevisaoEmDia(!(atrasadaPorData || atrasadaPorHoras));
            veiculoRepository.save(veiculo);
        }
    }
}