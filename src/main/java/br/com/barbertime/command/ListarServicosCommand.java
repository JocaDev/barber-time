package br.com.barbertime.command;

import br.com.barbertime.dto.ServicoResponseDTO;
import br.com.barbertime.model.Servico;

import java.util.ArrayList;
import java.util.List;


public class ListarServicosCommand implements Command {

    private final ContextoAplicacao ctx;

    public ListarServicosCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        List<ServicoResponseDTO> saida = new ArrayList<>();
        for (Servico s : ctx.catalogo.listarServicos()) {
            saida.add(Mapeador.paraDto(s));
        }
        return Resposta.ok(saida);
    }
}
