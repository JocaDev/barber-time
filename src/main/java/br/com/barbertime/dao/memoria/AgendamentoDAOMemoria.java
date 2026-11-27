package br.com.barbertime.dao.memoria;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.StatusAgendamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AgendamentoDAOMemoria implements AgendamentoDAO {

    private final List<Agendamento> agendamentos = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public synchronized Agendamento inserir(Agendamento agendamento) {
        agendamento.setId(proximoId++);
        agendamentos.add(agendamento);
        return agendamento;
    }

    @Override
    public synchronized void atualizar(Agendamento agendamento) {
        // Em memória o objeto guardado é o mesmo que o service alterou; só confere se existe.
        if (buscarPorId(agendamento.getId()).isEmpty()) {
            throw new IllegalStateException("Agendamento " + agendamento.getId() + " não existe.");
        }
    }

    @Override
    public synchronized Optional<Agendamento> buscarPorId(Long id) {
        return agendamentos.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
    }

    @Override
    public synchronized List<Agendamento> listar(Long clienteId, Long profissionalId,
                                                 StatusAgendamento status) {
        List<Agendamento> saida = new ArrayList<>();
        for (Agendamento a : agendamentos) {
            boolean okCliente = clienteId == null || Objects.equals(a.getCliente().getId(), clienteId);
            boolean okProf = profissionalId == null
                    || Objects.equals(a.getProfissional().getId(), profissionalId);
            boolean okStatus = status == null || a.getStatus() == status;
            if (okCliente && okProf && okStatus) {
                saida.add(a);
            }
        }
        return saida;
    }

    @Override
    public synchronized List<Agendamento> listarPorProfissional(Long profissionalId) {
        return listar(null, profissionalId, null);
    }

    @Override
    public synchronized void remover(Long id) {
        agendamentos.removeIf(a -> Objects.equals(a.getId(), id));
    }
}
