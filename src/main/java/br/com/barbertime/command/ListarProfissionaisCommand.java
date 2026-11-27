package br.com.barbertime.command;

import br.com.barbertime.dto.ProfissionalResponseDTO;
import br.com.barbertime.model.Profissional;

import java.util.ArrayList;
import java.util.List;


public class ListarProfissionaisCommand implements Command {

    private final ContextoAplicacao ctx;

    public ListarProfissionaisCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        List<ProfissionalResponseDTO> saida = new ArrayList<>();
        for (Profissional p : ctx.catalogo.listarProfissionais()) {
            saida.add(Mapeador.paraDto(p));
        }
        return Resposta.ok(saida);
    }
}
