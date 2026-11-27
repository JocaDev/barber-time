package br.com.barbertime.command;

import br.com.barbertime.dto.ProfissionalRequestDTO;


public class CriarProfissionalCommand implements Command {

    private final ContextoAplicacao ctx;

    public CriarProfissionalCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        ProfissionalRequestDTO dto = req.corpo(ProfissionalRequestDTO.class);
        return Resposta.criado(Mapeador.paraDto(ctx.catalogo.cadastrar(dto)));
    }
}
