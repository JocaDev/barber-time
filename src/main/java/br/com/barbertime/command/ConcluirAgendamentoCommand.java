package br.com.barbertime.command;

import br.com.barbertime.model.Agendamento;


public class ConcluirAgendamentoCommand implements Command {

    private final ContextoAplicacao ctx;

    public ConcluirAgendamentoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        Agendamento a = ctx.agendamentos.buscarPorId(req.idDoCaminho());
        ctx.agendamentos.concluir(a);
        return Resposta.ok(Mapeador.paraDto(a));
    }
}
