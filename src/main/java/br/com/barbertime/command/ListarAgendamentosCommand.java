package br.com.barbertime.command;

import br.com.barbertime.model.Perfil;
import br.com.barbertime.model.StatusAgendamento;
import br.com.barbertime.model.Usuario;


public class ListarAgendamentosCommand implements Command {

    private final ContextoAplicacao ctx;

    public ListarAgendamentosCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        Usuario logado = req.getUsuario();
        Long profissionalId = req.queryLong("profissionalId");
        StatusAgendamento status = req.queryStatus("status");

        Long clienteId;
        if (logado.getPerfil() == Perfil.ADMINISTRADOR) {
            clienteId = req.queryLong("clienteId");
        } else {
            clienteId = logado.getId(); // cliente nunca vê agendamento de outro
        }
        return Resposta.ok(Mapeador.agendamentosParaDto(
                ctx.agendamentos.listar(clienteId, profissionalId, status)));
    }
}
