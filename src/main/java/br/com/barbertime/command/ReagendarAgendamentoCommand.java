package br.com.barbertime.command;

import br.com.barbertime.dto.ReagendamentoRequestDTO;
import br.com.barbertime.model.Agendamento;


public class ReagendarAgendamentoCommand implements Command {

    private final ContextoAplicacao ctx;

    public ReagendarAgendamentoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        Agendamento a = ctx.agendamentos.buscarPorId(req.idDoCaminho());
        AcessoAgendamento.garantir(req.getUsuario(), a);

        ReagendamentoRequestDTO dto = req.corpo(ReagendamentoRequestDTO.class);
        ctx.agendamentos.reagendar(a, dto.getNovaDataHora());
        return Resposta.ok(Mapeador.paraDto(a));
    }
}
