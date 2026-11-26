package br.com.barbertime.dao;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.model.*;

import java.time.LocalDateTime;

public class TestAgendamentoDAO {

    public static void main(String[] args) {

        try {
            Cliente cliente = new Cliente();
            cliente.setId(1L);

            Profissional profissional = new Profissional();
            profissional.setId(1L);

            Servico servico = new Servico();
            servico.setId(1L);
            servico.setDuracaoMinutos(40);

            Agendamento agendamento = new Agendamento(
                    null,
                    cliente,
                    profissional,
                    servico,
                    LocalDateTime.of(2026, 11, 27, 10, 0)
            );

            agendamento.confirmar();

            AgendamentoDAO dao = new AgendamentoDAO();
            dao.criar(agendamento);

            System.out.println("Agendamento criado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao criar agendamento:");
            e.printStackTrace();
        }
    }
}