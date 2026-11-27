package br.com.barbertime.command;


public class RemoverAgendamentoCommand implements Command {

    private final ContextoAplicacao ctx;

    public RemoverAgendamentoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        ctx.agendamentos.remover(req.idDoCaminho());
        return Resposta.semConteudo();
    }
}
