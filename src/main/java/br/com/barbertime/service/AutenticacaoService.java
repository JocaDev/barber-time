package br.com.barbertime.service;

import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.exception.NaoAutenticadoException;
import br.com.barbertime.exception.RecursoNaoEncontradoException;
import br.com.barbertime.model.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Login com token de sessão (em memória). O token vai no header Authorization: Bearer ... */
public class AutenticacaoService {

    public static final long VALIDADE_HORAS = 2;

    /** Sessão aberta: quem é o usuário e até quando o token vale. */
    public static class Sessao {
        private final String token;
        private final Usuario usuario;
        private final LocalDateTime expiraEm;

        Sessao(String token, Usuario usuario, LocalDateTime expiraEm) {
            this.token = token;
            this.usuario = usuario;
            this.expiraEm = expiraEm;
        }

        public String getToken() { return token; }
        public Usuario getUsuario() { return usuario; }
        public LocalDateTime getExpiraEm() { return expiraEm; }
    }

    private final UsuarioDAO usuarios;
    private final Map<String, Sessao> sessoes = new HashMap<>();

    public AutenticacaoService(UsuarioDAO usuarios) {
        this.usuarios = usuarios;
    }

    public synchronized Usuario buscarUsuario(Long id) {
        return usuarios.buscarPorId(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado."));
    }

    public synchronized Sessao login(String email, String senha) {
        if (email == null || senha == null) {
            throw new IllegalArgumentException("E-mail e senha são obrigatórios.");
        }
        Usuario u = usuarios.buscarPorEmail(email).orElse(null);
        if (u == null || !u.autenticar(email, hash(senha))) {
            throw new NaoAutenticadoException("E-mail ou senha inválidos.");
        }
        String token = UUID.randomUUID().toString();
        Sessao sessao = new Sessao(token, u, LocalDateTime.now().plusHours(VALIDADE_HORAS));
        sessoes.put(token, sessao);
        return sessao;
    }

    public synchronized Usuario usuarioDoToken(String token) {
        Sessao s = token == null ? null : sessoes.get(token);
        if (s == null) {
            throw new NaoAutenticadoException("Token ausente ou inválido. Faça login em POST /api/login.");
        }
        if (s.getExpiraEm().isBefore(LocalDateTime.now())) {
            sessoes.remove(token);
            throw new NaoAutenticadoException("Token expirado. Faça login novamente.");
        }
        return s.getUsuario();
    }

    /** SHA-256 em hexadecimal. (Para o trabalho basta; em produção usar bcrypt/argon2.) */
    public static String hash(String senha) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
