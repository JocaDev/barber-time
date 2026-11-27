package br.com.barbertime.dao.memoria;

import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.model.Servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServicoDAOMemoria implements ServicoDAO {

    private final List<Servico> servicos = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public synchronized Servico inserir(Servico servico) {
        servico.setId(proximoId++);
        servicos.add(servico);
        return servico;
    }

    @Override
    public synchronized Optional<Servico> buscarPorId(Long id) {
        return servicos.stream().filter(s -> Objects.equals(s.getId(), id)).findFirst();
    }

    @Override
    public synchronized List<Servico> listar() {
        return new ArrayList<>(servicos);
    }
}
