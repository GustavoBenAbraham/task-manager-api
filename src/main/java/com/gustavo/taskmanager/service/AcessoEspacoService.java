package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.AcessoEspacoResponseDTO;
import com.gustavo.taskmanager.dto.ConviteEspacoResponseDTO;
import com.gustavo.taskmanager.dto.EspacoFinanceiroResponseDTO;
import com.gustavo.taskmanager.exception.ConviteEspacoException;
import com.gustavo.taskmanager.exception.EspacoAcessoNegadoException;
import com.gustavo.taskmanager.model.*;
import com.gustavo.taskmanager.repository.AcessoEspacoRepository;
import com.gustavo.taskmanager.repository.ConviteEspacoRepository;
import com.gustavo.taskmanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AcessoEspacoService {

    private final AcessoEspacoRepository acessoRepository;
    private final ConviteEspacoRepository conviteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAtualService usuarioAtualService;

    @Transactional
    public ConviteEspacoResponseDTO convidarGestora(Long espacoId, String email) {
        Usuario proprietario = usuarioObrigatorio();
        AcessoEspaco acessoProprietario = obterAcessoAtivo(espacoId, proprietario);
        if (acessoProprietario.getPapel() != PapelAcessoEspaco.PROPRIETARIO
                || acessoProprietario.getEspaco().getTipo() != TipoEspacoFinanceiro.NEGOCIO
                || !proprietario.getId().equals(acessoProprietario.getEspaco().getProprietario().getId())) {
            throw new EspacoAcessoNegadoException();
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        if (emailNormalizado.equals(proprietario.getEmail().toLowerCase(Locale.ROOT))) {
            throw new ConviteEspacoException("Use outro e-mail para convidar a gestora financeira.");
        }
        usuarioRepository.findByEmailIgnoreCase(emailNormalizado).ifPresent(convidado -> {
            acessoRepository.findByEspacoAndUsuario(acessoProprietario.getEspaco(), convidado)
                .filter(AcessoEspaco::isAtivo)
                .ifPresent(acesso -> {
                    throw new ConviteEspacoException("Esta pessoa já tem acesso a este negócio.");
                });
        });

        conviteRepository.findAllByEspacoIdAndEmailConvidadoIgnoreCaseAndSituacao(
                espacoId, emailNormalizado, SituacaoConviteEspaco.PENDENTE)
            .forEach(convite -> convite.setSituacao(SituacaoConviteEspaco.REVOGADO));

        String token = gerarToken();
        LocalDateTime expiracao = LocalDateTime.now().plusDays(7);
        ConviteEspaco convite = conviteRepository.save(ConviteEspaco.builder()
            .espaco(acessoProprietario.getEspaco())
            .emailConvidado(emailNormalizado)
            .tokenHash(sha256(token))
            .papel(PapelAcessoEspaco.GESTORA_FINANCEIRA)
            .convidadoPor(proprietario)
            .dataExpiracao(expiracao)
            .build());

        return ConviteEspacoResponseDTO.builder()
            .id(convite.getId())
            .espacoId(espacoId)
            .nomeEspaco(convite.getEspaco().getNome())
            .emailConvidado(emailNormalizado)
            .token(token)
            .dataExpiracao(expiracao)
            .build();
    }

    @Transactional(noRollbackFor = ConviteEspacoException.class)
    public EspacoFinanceiroResponseDTO aceitarConvite(String token) {
        Usuario gestora = usuarioObrigatorio();
        ConviteEspaco convite = conviteRepository.findByTokenHash(sha256(token.trim()))
            .orElseThrow(() -> new ConviteEspacoException("Este convite não é válido."));

        if (convite.getSituacao() != SituacaoConviteEspaco.PENDENTE) {
            throw new ConviteEspacoException("Este convite já foi utilizado ou cancelado.");
        }
        if (!convite.getDataExpiracao().isAfter(LocalDateTime.now())) {
            convite.setSituacao(SituacaoConviteEspaco.EXPIRADO);
            throw new ConviteEspacoException("Este convite expirou. Peça ao cliente para enviar outro.");
        }
        if (!convite.getEmailConvidado().equalsIgnoreCase(gestora.getEmail())) {
            throw new ConviteEspacoException("Entre com o e-mail que recebeu o convite para aceitá-lo.");
        }

        AcessoEspaco acesso = acessoRepository.findByEspacoAndUsuario(convite.getEspaco(), gestora)
            .orElseGet(() -> AcessoEspaco.builder()
                .espaco(convite.getEspaco())
                .usuario(gestora)
                .papel(PapelAcessoEspaco.GESTORA_FINANCEIRA)
                .build());
        acesso.setPapel(PapelAcessoEspaco.GESTORA_FINANCEIRA);
        acesso.setAtivo(true);
        acessoRepository.save(acesso);

        convite.setSituacao(SituacaoConviteEspaco.ACEITO);
        convite.setAceitoPor(gestora);
        convite.setDataAceite(LocalDateTime.now());

        return EspacoFinanceiroResponseDTO.builder()
            .id(convite.getEspaco().getId())
            .nome(convite.getEspaco().getNome())
            .tipo(convite.getEspaco().getTipo())
            .cnpj(convite.getEspaco().getCnpj())
            .papel(PapelAcessoEspaco.GESTORA_FINANCEIRA)
            .build();
    }

    @Transactional(readOnly = true)
    public List<AcessoEspacoResponseDTO> listarAcessos(Long espacoId) {
        obterAcessoAtivo(espacoId, usuarioObrigatorio());
        return acessoRepository.listarAtivosComUsuarios(espacoId).stream()
            .map(acesso -> AcessoEspacoResponseDTO.builder()
                .usuarioId(acesso.getUsuario().getId())
                .nome(acesso.getUsuario().getNome())
                .email(acesso.getUsuario().getEmail())
                .papel(acesso.getPapel())
                .build())
            .toList();
    }

    @Transactional
    public void removerGestora(Long espacoId, Long usuarioId) {
        Usuario proprietario = usuarioObrigatorio();
        AcessoEspaco acessoProprietario = obterAcessoAtivo(espacoId, proprietario);
        if (acessoProprietario.getPapel() != PapelAcessoEspaco.PROPRIETARIO
                || !proprietario.getId().equals(acessoProprietario.getEspaco().getProprietario().getId())) {
            throw new EspacoAcessoNegadoException();
        }

        AcessoEspaco acessoGestora = acessoRepository.findByEspacoIdAndUsuarioAndAtivoTrue(
                espacoId, usuarioRepository.findById(usuarioId).orElseThrow(EspacoAcessoNegadoException::new))
            .orElseThrow(EspacoAcessoNegadoException::new);
        if (acessoGestora.getPapel() != PapelAcessoEspaco.GESTORA_FINANCEIRA) {
            throw new EspacoAcessoNegadoException();
        }
        acessoGestora.setAtivo(false);
        conviteRepository.findAllByEspacoIdAndEmailConvidadoIgnoreCaseAndSituacao(
                espacoId, acessoGestora.getUsuario().getEmail(), SituacaoConviteEspaco.PENDENTE)
            .forEach(convite -> convite.setSituacao(SituacaoConviteEspaco.REVOGADO));
    }

    private AcessoEspaco obterAcessoAtivo(Long espacoId, Usuario usuario) {
        return acessoRepository.findByEspacoIdAndUsuarioAndAtivoTrue(espacoId, usuario)
            .orElseThrow(EspacoAcessoNegadoException::new);
    }

    private Usuario usuarioObrigatorio() {
        Usuario usuario = usuarioAtualService.obter();
        if (usuario == null) throw new EspacoAcessoNegadoException();
        return usuario;
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Não foi possível proteger o convite.", e);
        }
    }
}
