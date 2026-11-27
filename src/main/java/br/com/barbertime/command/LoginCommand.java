package br.com.barbertime.command;

import br.com.barbertime.dto.LoginRequestDTO;
import br.com.barbertime.dto.LoginResponseDTO;
import br.com.barbertime.service.AutenticacaoService.Sessao;


public class LoginCommand implements Command {

    private final ContextoAplicacao ctx;

    public LoginCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        LoginRequestDTO dto = req.corpo(LoginRequestDTO.class);
        Sessao sessao = ctx.autenticacao.login(dto.getEmail(), dto.getSenha());

        LoginResponseDTO resposta = new LoginResponseDTO();
        resposta.setToken(sessao.getToken());
        resposta.setTipoToken("Bearer");
        resposta.setExpiraEm(sessao.getExpiraEm());
        resposta.setUsuarioId(sessao.getUsuario().getId());
        resposta.setNome(sessao.getUsuario().getNome());
        resposta.setPerfil(sessao.getUsuario().getPerfil());
        return Resposta.ok(resposta);
    }
}
