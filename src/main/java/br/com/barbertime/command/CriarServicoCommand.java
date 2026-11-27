package br.com.barbertime.command;

import br.com.barbertime.dto.ServicoRequestDTO;


public class CriarServicoCommand implements Command {

    private final ContextoAplicacao ctx;

    public CriarServicoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        ServicoRequestDTO dto = req.corpo(ServicoRequestDTO.class);
        return Resposta.criado(Mapeador.paraDto(ctx.catalogo.cadastrar(dto)));
    }
}
