package br.com.barbertime.dao;

import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.StatusAgendamento;

import java.util.List;
import java.util.Optional;


public interface AgendamentoDAO {

    
    Agendamento inserir(Agendamento agendamento);

    
    void atualizar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(Long id);

    
    List<Agendamento> listar(Long clienteId, Long profissionalId, StatusAgendamento status);

    List<Agendamento> listarPorProfissional(Long profissionalId);

   
    void remover(Long id);
}
