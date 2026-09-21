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
import com.gustavo.taskmanager.service.LancamentoService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LancamentoServiceImpl implements LancamentoService {

    private final LancamentoRepository repository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAtualService usuarioAtualService;

    @Override
    public LancamentoResponseDTO criar(LancamentoRequestDTO dto) {
        log.info("Criando lançamento: {}", dto.getDescricao());
        Categoria categoria = buscarCategoria(dto.getCategoriaId());
        Lancamento lancamento = Lancamento.builder()
            .descricao(dto.getDescricao())
            .valor(dto.getValor())
            .tipo(dto.getTipo())
            .data(dto.getData())
            .categoria(categoria != null ? categoria.getNome() : dto.getCategoria())
            .categoriaRelacionada(categoria)
            .conta(buscarConta(dto.getContaId()))
            .usuario(usuarioAtual())
            .observacao(dto.getObservacao())
            .build();

        LancamentoResponseDTO response = toDTO(repository.save(lancamento));
        log.info("Lançamento criado com sucesso: id={}", response.getId());
        return response;
    }

    @Override
    public List<LancamentoResponseDTO> listarTodos() {
        log.debug("Listando todos os lançamentos");
        Usuario usuario = usuarioAtual();
        List<Lancamento> lancamentos = usuario == null
            ? repository.findAllByOrderByDataDesc()
            : repository.findAllByUsuarioOrderByDataDesc(usuario);
        return lancamentos
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
        Usuario usuario = usuarioAtual();
        List<Lancamento> lancamentos = usuario == null
            ? repository.findByTipoOrderByDataDesc(tipo)
            : repository.findByUsuarioAndTipoOrderByDataDesc(usuario, tipo);
        return lancamentos
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public LancamentoResponseDTO atualizar(Long id, LancamentoRequestDTO dto) {
        log.info("Atualizando lançamento: id={}", id);
        Lancamento lancamento = obterLancamento(id);
        Categoria categoria = buscarCategoria(dto.getCategoriaId());

        lancamento.setDescricao(dto.getDescricao());
        lancamento.setValor(dto.getValor());
        lancamento.setTipo(dto.getTipo());
        lancamento.setData(dto.getData());
        lancamento.setCategoria(categoria != null ? categoria.getNome() : dto.getCategoria());
        lancamento.setCategoriaRelacionada(categoria);
        lancamento.setConta(buscarConta(dto.getContaId()));
        lancamento.setObservacao(dto.getObservacao());

        LancamentoResponseDTO response = toDTO(repository.save(lancamento));
        log.info("Lançamento atualizado com sucesso: id={}", id);
        return response;
    }

    @Override
    public void deletar(Long id) {
        log.info("Deletando lançamento: id={}", id);
        obterLancamento(id);
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
            .dataCriacao(lancamento.getDataCriacao())
            .dataAtualizacao(lancamento.getDataAtualizacao())
            .build();
    }

    private Conta buscarConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
            .orElseThrow(() -> new ContaNotFoundException(contaId));
        Usuario usuario = usuarioAtual();
        if (usuario != null && (conta.getUsuario() == null
                || !usuario.getId().equals(conta.getUsuario().getId()))) {
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
        Usuario usuario = usuarioAtual();
        if (usuario != null && (categoria.getUsuario() == null
                || !usuario.getId().equals(categoria.getUsuario().getId()))) {
            throw new CategoriaNotFoundException(categoriaId);
        }
        return categoria;
    }

    private Lancamento obterLancamento(Long id) {
        Lancamento lancamento = repository.findById(id)
            .orElseThrow(() -> new LancamentoNotFoundException(id));
        Usuario usuario = usuarioAtual();
        if (usuario != null && (lancamento.getUsuario() == null
                || !usuario.getId().equals(lancamento.getUsuario().getId()))) {
            throw new LancamentoNotFoundException(id);
        }
        return lancamento;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService.obter();
    }

}
