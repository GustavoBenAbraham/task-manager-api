package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.LancamentoRequestDTO;
import com.gustavo.taskmanager.dto.LancamentoResponseDTO;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.exception.LancamentoNotFoundException;
import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.TipoLancamento;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.repository.TituloFinanceiroRepository;
import com.gustavo.taskmanager.exception.OperacaoFinanceiraException;
import com.gustavo.taskmanager.service.LancamentoService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import com.gustavo.taskmanager.service.EspacoAtualService;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LancamentoServiceImpl implements LancamentoService {

    private final LancamentoRepository repository;
    private final TituloFinanceiroRepository tituloRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAtualService usuarioAtualService;
    private final EspacoAtualService espacoAtualService;

    @Override
    public LancamentoResponseDTO criar(LancamentoRequestDTO dto) {
        log.info("Criando lançamento: {}", dto.getDescricao());
        EspacoFinanceiro espaco = espacoAtualService.obter();
        Usuario usuario = usuarioAtual();
        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Lancamento lancamento = Lancamento.builder()
            .descricao(dto.getDescricao())
            .valor(dto.getValor())
            .tipo(dto.getTipo())
            .data(dto.getData())
            .categoria(categoria != null ? categoria.getNome() : dto.getCategoria())
            .categoriaRelacionada(categoria)
            .conta(buscarConta(dto.getContaId()))
            .usuario(usuario)
            .atualizadoPor(usuario)
            .espaco(espaco)
            .observacao(dto.getObservacao())
            .build();

        LancamentoResponseDTO response = toDTO(repository.save(lancamento));
        log.info("Lançamento criado com sucesso: id={}", response.getId());
        return response;
    }

    @Override
    public List<LancamentoResponseDTO> listarTodos() {
        log.debug("Listando todos os lançamentos");
        return repository.findAllByEspacoOrderByDataDesc(espacoAtualService.obter())
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public LancamentoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando lançamento por id: {}", id);
        return toDTO(obterLancamento(id));
    }

    @Override
    public List<LancamentoResponseDTO> buscarPorTipo(TipoLancamento tipo) {
        log.debug("Buscando lançamentos por tipo: {}", tipo);
        return repository.findByEspacoAndTipoOrderByDataDesc(espacoAtualService.obter(), tipo)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public LancamentoResponseDTO atualizar(Long id, LancamentoRequestDTO dto) {
        Lancamento lancamento = obterLancamento(id);
        if (tituloRepository.existsByLancamentoGerado_Id(id)) {
            throw new OperacaoFinanceiraException("Esta movimentação pertence a uma conta liquidada e não pode ser editada aqui.");
        }
        log.info("Atualizando lançamento: id={}", id);
        Categoria categoria = buscarCategoria(dto.getCategoriaId());

        lancamento.setDescricao(dto.getDescricao());
        lancamento.setValor(dto.getValor());
        lancamento.setTipo(dto.getTipo());
        lancamento.setData(dto.getData());
        lancamento.setCategoria(categoria != null ? categoria.getNome() : dto.getCategoria());
        lancamento.setCategoriaRelacionada(categoria);
        lancamento.setConta(buscarConta(dto.getContaId()));
        lancamento.setObservacao(dto.getObservacao());
        lancamento.setAtualizadoPor(usuarioAtual());

        LancamentoResponseDTO response = toDTO(repository.save(lancamento));
        log.info("Lançamento atualizado com sucesso: id={}", id);
        return response;
    }

    @Override
    public void deletar(Long id) {
        obterLancamento(id);
        if (tituloRepository.existsByLancamentoGerado_Id(id)) {
            throw new OperacaoFinanceiraException("Esta movimentação pertence a uma conta liquidada e não pode ser excluída aqui.");
        }
        log.info("Deletando lançamento: id={}", id);
        repository.deleteById(id);
        log.info("Lançamento deletado com sucesso: id={}", id);
    }

    private LancamentoResponseDTO toDTO(Lancamento lancamento) {
        return LancamentoResponseDTO.builder()
            .id(lancamento.getId())
            .descricao(lancamento.getDescricao())
            .valor(lancamento.getValor())
            .tipo(lancamento.getTipo())
            .data(lancamento.getData())
            .categoria(lancamento.getCategoria())
            .categoriaId(lancamento.getCategoriaRelacionada() != null ? lancamento.getCategoriaRelacionada().getId() : null)
            .categoriaNome(lancamento.getCategoriaRelacionada() != null ? lancamento.getCategoriaRelacionada().getNome() : null)
            .contaId(lancamento.getConta() != null ? lancamento.getConta().getId() : null)
            .contaNome(lancamento.getConta() != null ? lancamento.getConta().getNome() : null)
            .observacao(lancamento.getObservacao())
            .criadoPorUsuarioId(lancamento.getUsuario() == null ? null : lancamento.getUsuario().getId())
            .atualizadoPorUsuarioId(lancamento.getAtualizadoPor() == null ? null : lancamento.getAtualizadoPor().getId())
            .geradoDeContaPrevista(tituloRepository.existsByLancamentoGerado_Id(lancamento.getId()))
            .dataCriacao(lancamento.getDataCriacao())
            .dataAtualizacao(lancamento.getDataAtualizacao())
            .build();
    }

    private Conta buscarConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
            .orElseThrow(() -> new ContaNotFoundException(contaId));
        EspacoFinanceiro espaco = espacoAtualService.obter();
        if (conta.getEspaco() == null || !espaco.getId().equals(conta.getEspaco().getId())) {
            throw new ContaNotFoundException(contaId);
        }
        return conta;
    }

    private Categoria buscarCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));
        EspacoFinanceiro espaco = espacoAtualService.obter();
        if (categoria.getEspaco() == null || !espaco.getId().equals(categoria.getEspaco().getId())) {
            throw new CategoriaNotFoundException(categoriaId);
        }
        return categoria;
    }

    private Lancamento obterLancamento(Long id) {
        Lancamento lancamento = repository.findById(id)
            .orElseThrow(() -> new LancamentoNotFoundException(id));
        EspacoFinanceiro espaco = espacoAtualService.obter();
        if (lancamento.getEspaco() == null || !espaco.getId().equals(lancamento.getEspaco().getId())) {
            throw new LancamentoNotFoundException(id);
        }
        return lancamento;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService.obter();
    }

}
