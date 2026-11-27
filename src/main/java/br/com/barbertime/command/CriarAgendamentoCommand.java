package br.com.barbertime.command;

import br.com.barbertime.dto.AgendamentoRequestDTO;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.Perfil;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;
import br.com.barbertime.model.Usuario;


public class CriarAgendamentoCommand implements Command {

    private final ContextoAplicacao ctx;

    public CriarAgendamentoCommand(ContextoAplicacao ctx) {
        this.ctx = ctx;
    }

    @Override
    public Resposta executar(Requisicao req) {
        AgendamentoRequestDTO dto = req.corpo(AgendamentoRequestDTO.class);
        Usuario logado = req.getUsuario();

        Cliente cliente;
        if (logado.getPerfil() == Perfil.ADMINISTRADOR) {
            if (dto.getClienteId() == null) {
                throw new IllegalArgumentException("O administrador deve informar o clienteId.");
            }
            Usuario alvo = ctx.autenticacao.buscarUsuario(dto.getClienteId());
            if (!(alvo instanceof Cliente)) {
                throw new IllegalArgumentException("O clienteId informado não é de um cliente.");
            }
            cliente = (Cliente) alvo;
        } else {
            cliente = (Cliente) logado;
        }

        if (dto.getProfissionalId() == null || dto.getServicoId() == null
                || dto.getDataHoraInicio() == null) {
            throw new IllegalArgumentException(
                    "Informe profissionalId, servicoId e dataHoraInicio (RN02).");
        }
        Profissional profissional = ctx.catalogo.buscarProfissional(dto.getProfissionalId());
        Servico servico = ctx.catalogo.buscarServico(dto.getServicoId());

        Agendamento criado = ctx.agendamentos.agendar(
                cliente, profissional, servico, dto.getDataHoraInicio());
        return Resposta.criado(Mapeador.paraDto(criado)); 
    }
}
