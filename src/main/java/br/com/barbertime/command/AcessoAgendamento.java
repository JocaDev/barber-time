package br.com.barbertime.command;

import br.com.barbertime.exception.AcessoNegadoException;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Perfil;
import br.com.barbertime.model.Usuario;

import java.util.Objects;


final class AcessoAgendamento {

    private AcessoAgendamento() {
    }

    static void garantir(Usuario logado, Agendamento agendamento) {
        if (logado.getPerfil() == Perfil.ADMINISTRADOR) {
            return;
        }
        if (!Objects.equals(agendamento.getCliente().getId(), logado.getId())) {
            throw new AcessoNegadoException("Este agendamento pertence a outro cliente.");
        }
    }
}
