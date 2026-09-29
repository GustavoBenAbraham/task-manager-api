package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.LiquidarTituloFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.TituloFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.TituloFinanceiroResponseDTO;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.exception.TituloFinanceiroNotFoundException;
import com.gustavo.taskmanager.exception.OperacaoFinanceiraException;
import com.gustavo.taskmanager.model.*;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.repository.TituloFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TituloFinanceiroService {

    private final TituloFinanceiroRepository tituloRepository;
    private final LancamentoRepository lancamentoRepository;
    private final ContaRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final EspacoAtualService espacoAtualService;
    private final UsuarioAtualService usuarioAtualService;

    @Transactional(readOnly = true)
    public List<TituloFinanceiroResponseDTO> listar() {
        return tituloRepository.findAllByEspacoOrderByDataVencimentoAsc(espacoAtualService.obter())
            .stream().map(this::toDTO).toList();
    }

    @Transactional
    public TituloFinanceiroResponseDTO criar(TituloFinanceiroRequestDTO dto) {
        Usuario usuario = usuarioAtualService.obter();
        TituloFinanceiro titulo = TituloFinanceiro.builder()
            .espaco(espacoAtualService.obter())
            .usuario(usuario)
            .atualizadoPor(usuario)
            .descricao(dto.getDescricao().trim())
            .valor(dto.getValor())
            .tipo(dto.getTipo())
            .dataVencimento(dto.getDataVencimento())
            .categoria(dto.getCategoria().trim())
            .observacao(dto.getObservacao())
            .situacao(SituacaoTituloFinanceiro.PENDENTE)
            .build();
        aplicarCategoria(titulo, dto.getCategoriaId());
        return toDTO(tituloRepository.save(titulo));
    }

    @Transactional
    public TituloFinanceiroResponseDTO atualizar(Long id, TituloFinanceiroRequestDTO dto) {
        TituloFinanceiro titulo = obterParaAtualizacao(id);
        if (titulo.getSituacao() != SituacaoTituloFinanceiro.PENDENTE) {
            throw new OperacaoFinanceiraException("Somente contas pendentes podem ser editadas.");
        }
        titulo.setDescricao(dto.getDescricao().trim());
        titulo.setValor(dto.getValor());
        titulo.setTipo(dto.getTipo());
        titulo.setDataVencimento(dto.getDataVencimento());
        titulo.setCategoria(dto.getCategoria().trim());
        titulo.setObservacao(dto.getObservacao());
        titulo.setAtualizadoPor(usuarioAtualService.obter());
        aplicarCategoria(titulo, dto.getCategoriaId());
        return toDTO(tituloRepository.save(titulo));
    }

    @Transactional
    public TituloFinanceiroResponseDTO liquidar(Long id, LiquidarTituloFinanceiroRequestDTO dto) {
        TituloFinanceiro titulo = obterParaAtualizacao(id);
        if (titulo.getSituacao() != SituacaoTituloFinanceiro.PENDENTE) {
            throw new OperacaoFinanceiraException("Esta conta já foi liquidada ou cancelada.");
        }

        EspacoFinanceiro espaco = titulo.getEspaco();
        Conta conta = contaRepository.findById(dto.getContaId())
            .filter(Conta::isAtivo)
            .filter(c -> c.getEspaco() != null && espaco.getId().equals(c.getEspaco().getId()))
            .orElseThrow(() -> new ContaNotFoundException(dto.getContaId()));
        Usuario usuario = usuarioAtualService.obter();

        Lancamento lancamento = Lancamento.builder()
            .descricao(titulo.getDescricao())
            .valor(titulo.getValor())
            .tipo(titulo.getTipo() == TipoTituloFinanceiro.A_RECEBER
                ? TipoLancamento.RECEITA : TipoLancamento.DESPESA)
            .data(dto.getDataLiquidacao())
            .categoria(titulo.getCategoria())
            .categoriaRelacionada(titulo.getCategoriaRelacionada())
            .conta(conta)
            .observacao(titulo.getObservacao())
            .usuario(usuario)
            .atualizadoPor(usuario)
            .espaco(espaco)
            .build();
        lancamento = lancamentoRepository.save(lancamento);

        titulo.setSituacao(titulo.getTipo() == TipoTituloFinanceiro.A_RECEBER
            ? SituacaoTituloFinanceiro.RECEBIDO : SituacaoTituloFinanceiro.PAGO);
        titulo.setDataLiquidacao(dto.getDataLiquidacao());
        titulo.setContaLiquidacao(conta);
        titulo.setLancamentoGerado(lancamento);
        titulo.setAtualizadoPor(usuario);
        return toDTO(tituloRepository.save(titulo));
    }

    @Transactional
    public TituloFinanceiroResponseDTO cancelar(Long id) {
        TituloFinanceiro titulo = obterParaAtualizacao(id);
        if (titulo.getSituacao() != SituacaoTituloFinanceiro.PENDENTE) {
            throw new OperacaoFinanceiraException("Somente contas pendentes podem ser canceladas.");
        }
        titulo.setSituacao(SituacaoTituloFinanceiro.CANCELADO);
        titulo.setAtualizadoPor(usuarioAtualService.obter());
        return toDTO(tituloRepository.save(titulo));
    }

    private TituloFinanceiro obterParaAtualizacao(Long id) {
        return tituloRepository.findByIdAndEspacoForUpdate(id, espacoAtualService.obter())
            .orElseThrow(() -> new TituloFinanceiroNotFoundException(id));
    }

    private void aplicarCategoria(TituloFinanceiro titulo, Long categoriaId) {
        if (categoriaId == null) {
            titulo.setCategoriaRelacionada(null);
            return;
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .filter(c -> c.getEspaco() != null && titulo.getEspaco().getId().equals(c.getEspaco().getId()))
            .orElseThrow(() -> new CategoriaNotFoundException(categoriaId));
        titulo.setCategoria(categoria.getNome());
        titulo.setCategoriaRelacionada(categoria);
    }

    private TituloFinanceiroResponseDTO toDTO(TituloFinanceiro titulo) {
        return TituloFinanceiroResponseDTO.builder()
            .id(titulo.getId())
            .descricao(titulo.getDescricao())
            .valor(titulo.getValor())
            .tipo(titulo.getTipo())
            .dataVencimento(titulo.getDataVencimento())
            .categoria(titulo.getCategoria())
            .categoriaId(titulo.getCategoriaRelacionada() == null ? null : titulo.getCategoriaRelacionada().getId())
            .observacao(titulo.getObservacao())
            .situacao(titulo.getSituacao())
            .dataLiquidacao(titulo.getDataLiquidacao())
            .contaLiquidacaoId(titulo.getContaLiquidacao() == null ? null : titulo.getContaLiquidacao().getId())
            .contaLiquidacaoNome(titulo.getContaLiquidacao() == null ? null : titulo.getContaLiquidacao().getNome())
            .lancamentoGeradoId(titulo.getLancamentoGerado() == null ? null : titulo.getLancamentoGerado().getId())
            .criadoPorUsuarioId(titulo.getUsuario() == null ? null : titulo.getUsuario().getId())
            .atualizadoPorUsuarioId(titulo.getAtualizadoPor() == null ? null : titulo.getAtualizadoPor().getId())
            .build();
    }
}
