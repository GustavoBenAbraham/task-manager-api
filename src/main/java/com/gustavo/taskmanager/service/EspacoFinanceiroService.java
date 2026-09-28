package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.EspacoFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.EspacoFinanceiroResponseDTO;
import com.gustavo.taskmanager.exception.EspacoJaExisteException;
import com.gustavo.taskmanager.model.AcessoEspaco;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.PapelAcessoEspaco;
import com.gustavo.taskmanager.model.TipoEspacoFinanceiro;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.AcessoEspacoRepository;
import com.gustavo.taskmanager.repository.EspacoFinanceiroRepository;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspacoFinanceiroService {

    private final EspacoFinanceiroRepository espacoRepository;
    private final AcessoEspacoRepository acessoRepository;
    private final UsuarioAtualService usuarioAtualService;

    @Transactional(readOnly = true)
    public List<EspacoFinanceiroResponseDTO> listarMeusEspacos() {
        Usuario usuario = usuarioAtualService.obter();
        if (usuario == null) {
            return List.of();
        }
        return acessoRepository.listarAtivosComEspaco(usuario).stream()
            .filter(acesso -> acesso.getEspaco().isAtivo())
            .map(acesso -> toDTO(acesso.getEspaco(), acesso.getPapel()))
            .toList();
    }

    @Transactional
    public EspacoFinanceiroResponseDTO criarNegocio(EspacoFinanceiroRequestDTO dto) {
        Usuario proprietario = usuarioAtualService.obter();
        if (proprietario == null) {
            throw new IllegalStateException("É necessário entrar na sua conta para criar um espaço.");
        }
        if (dto.getTipo() != TipoEspacoFinanceiro.NEGOCIO) {
            throw new IllegalArgumentException("Nesta etapa, só é possível criar um espaço do tipo negócio.");
        }
        if (espacoRepository.existsByProprietarioAndTipo(proprietario, TipoEspacoFinanceiro.NEGOCIO)) {
            throw new EspacoJaExisteException("negócio");
        }

        String cnpj = normalizarCnpj(dto.getCnpj());
        EspacoFinanceiro espaco = espacoRepository.save(EspacoFinanceiro.builder()
            .proprietario(proprietario)
            .nome(dto.getNome().trim())
            .tipo(TipoEspacoFinanceiro.NEGOCIO)
            .cnpj(cnpj)
            .build());
        acessoRepository.save(AcessoEspaco.builder()
            .espaco(espaco)
            .usuario(proprietario)
            .papel(PapelAcessoEspaco.PROPRIETARIO)
            .build());

        return toDTO(espaco, PapelAcessoEspaco.PROPRIETARIO);
    }

    @Transactional
    public void criarEspacoPessoal(Usuario proprietario) {
        if (espacoRepository.existsByProprietarioAndTipo(proprietario, TipoEspacoFinanceiro.PESSOAL)) {
            return;
        }
        EspacoFinanceiro espaco = espacoRepository.save(EspacoFinanceiro.builder()
            .proprietario(proprietario)
            .nome("Pessoal")
            .tipo(TipoEspacoFinanceiro.PESSOAL)
            .build());
        acessoRepository.save(AcessoEspaco.builder()
            .espaco(espaco)
            .usuario(proprietario)
            .papel(PapelAcessoEspaco.PROPRIETARIO)
            .build());
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }
        String digitos = cnpj.replaceAll("\\D", "");
        if (digitos.length() != 14) {
            throw new IllegalArgumentException("O CNPJ deve conter 14 dígitos ou ficar em branco.");
        }
        return digitos;
    }

    private EspacoFinanceiroResponseDTO toDTO(EspacoFinanceiro espaco, PapelAcessoEspaco papel) {
        return EspacoFinanceiroResponseDTO.builder()
            .id(espaco.getId())
            .nome(espaco.getNome())
            .tipo(espaco.getTipo())
            .cnpj(espaco.getCnpj())
            .papel(papel)
            .build();
    }
}
