package br.com.barbertime.dao;

import br.com.barbertime.model.Profissional;

import java.util.List;
import java.util.Optional;


public interface ProfissionalDAO {

    Profissional inserir(Profissional profissional);

    Optional<Profissional> buscarPorId(Long id);

    List<Profissional> listar();
}
