package br.com.barbertime.dao;

import br.com.barbertime.model.Servico;

import java.util.List;
import java.util.Optional;


public interface ServicoDAO {

    Servico inserir(Servico servico);

    Optional<Servico> buscarPorId(Long id);

    List<Servico> listar();
}
