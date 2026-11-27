package br.com.barbertime.dao.memoria;

import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.model.Profissional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProfissionalDAOMemoria implements ProfissionalDAO {

    private final List<Profissional> profissionais = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public synchronized Profissional inserir(Profissional profissional) {
        profissional.setId(proximoId++);
        profissionais.add(profissional);
        return profissional;
    }

    @Override
    public synchronized Optional<Profissional> buscarPorId(Long id) {
        return profissionais.stream().filter(p -> Objects.equals(p.getId(), id)).findFirst();
    }

    @Override
    public synchronized List<Profissional> listar() {
        return new ArrayList<>(profissionais);
    }
}
