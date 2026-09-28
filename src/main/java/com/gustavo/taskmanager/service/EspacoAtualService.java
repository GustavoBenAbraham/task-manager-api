package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.exception.EspacoAcessoNegadoException;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.TipoEspacoFinanceiro;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.AcessoEspacoRepository;
import com.gustavo.taskmanager.repository.EspacoFinanceiroRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class EspacoAtualService {

    public static final String HEADER_ESPACO = "X-Espaco-Financeiro-Id";

    private final UsuarioAtualService usuarioAtualService;
    private final AcessoEspacoRepository acessoRepository;
    private final EspacoFinanceiroRepository espacoRepository;

    public EspacoFinanceiro obter() {
        Usuario usuario = usuarioAtualService.obter();
        if (usuario == null) {
            throw new EspacoAcessoNegadoException();
        }

        String espacoId = obterIdDoCabecalho();
        if (espacoId == null || espacoId.isBlank()) {
            return espacoRepository.findByProprietarioAndTipo(usuario, TipoEspacoFinanceiro.PESSOAL)
                .filter(espaco -> acessoRepository.existsByEspacoAndUsuarioAndAtivoTrue(espaco, usuario))
                .orElseThrow(EspacoAcessoNegadoException::new);
        }

        try {
            Long id = Long.valueOf(espacoId);
            return acessoRepository.findByEspacoIdAndUsuarioAndAtivoTrue(id, usuario)
                .map(acesso -> acesso.getEspaco())
                .filter(EspacoFinanceiro::isAtivo)
                .orElseThrow(EspacoAcessoNegadoException::new);
        } catch (NumberFormatException ex) {
            throw new EspacoAcessoNegadoException();
        }
    }

    private String obterIdDoCabecalho() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(HEADER_ESPACO);
    }
}
