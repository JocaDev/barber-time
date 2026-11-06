import client.ViaCepClient;
import model.*;
import service.AgendamentoService;
import service.EnderecoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Demonstração: consulta o ViaCEP e testa as regras de agendamento. */
public class Main {

    public static void main(String[] args) {
        String cep = args.length > 0 ? args[0] : "48110-000";

        // 1) Consulta o ViaCEP
        EnderecoService enderecoService = new EnderecoService(new ViaCepClient());
        Endereco endereco;
        try {
            endereco = enderecoService.buscarPorCep(cep);
            System.out.println("Endereço encontrado: " + endereco);
        } catch (Exception e) {
            System.out.println("Não foi possível consultar o ViaCEP: " + e);
            endereco = new Endereco(cep, "(offline)", "", "", "", "");
        }

        // 2) Monta barbearia, profissional e serviço
        Administrador admin = new Administrador(1L, "Admin", "admin@barbearia.com", "hash", "71999990000");
        Barbearia barbearia = new Barbearia(1L, "Barbearia Exemplo", "00.000.000/0001-00",
                "7133330000", "100", endereco, admin);

        Profissional joao = new Profissional(1L, "João", "Corte masculino", "71988880000");
        barbearia.adicionarProfissional(joao);
        joao.adicionarDisponibilidade(new Disponibilidade(DiaSemana.SEGUNDA,
                LocalTime.of(9, 0), LocalTime.of(18, 0)));

        Servico corte = new Servico(1L, "Corte", "Corte tradicional", new BigDecimal("35.00"), 30);
        barbearia.adicionarServico(corte);

        // 3) Agenda, testa conflito, reagenda e cancela
        Cliente cliente = new Cliente(2L, "Maria", "maria@email.com", "hash", "71977770000");
        Cliente outro = new Cliente(3L, "Carlos", "carlos@email.com", "hash", "71966660000");
        AgendamentoService agenda = new AgendamentoService();

        // segunda-feira
        LocalDateTime horario = LocalDateTime.of(2026, 11, 9, 10, 0);
        Agendamento a1 = agenda.agendar(cliente, joao, corte, horario);
        System.out.println("Criado: " + a1);

        try {
            agenda.agendar(outro, joao, corte, horario);
        } catch (IllegalArgumentException e) {
            System.out.println("Conflito bloqueado: " + e.getMessage());
        }

        agenda.reagendar(a1, horario.plusHours(1));
        System.out.println("Reagendado: " + a1);

        agenda.cancelar(a1);
        System.out.println("Cancelado: " + a1);

        Agendamento a2 = agenda.agendar(outro, joao, corte, horario);
        System.out.println("Horário liberado e reutilizado: " + a2);
    }
}
