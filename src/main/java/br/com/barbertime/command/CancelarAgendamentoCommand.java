package br.com.barbertime.command;

import br.com.barbertime.model.Agendamento;


public class CancelarAgendamentoCommand implements Command {

    private final ContextoAplicacao ctx;

    public CancelarAgendamentoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        Agendamento a = ctx.agendamentos.buscarPorId(req.idDoCaminho());
        AcessoAgendamento.garantir(req.getUsuario(), a);
        ctx.agendamentos.cancelar(a);
        return Resposta.ok(Mapeador.paraDto(a));
    }
}
