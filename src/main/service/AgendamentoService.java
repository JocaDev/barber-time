package service;

import model.Agendamento;
import model.Cliente;
import model.DiaSemana;
import model.Disponibilidade;
import model.Profissional;
import model.Servico;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Regras de agendamento. Guarda em memória por enquanto (depois vira Repository). */
public class AgendamentoService {

    private final List<Agendamento> agendamentos = new ArrayList<>();
    private long proximoId = 1;

    public Agendamento agendar(Cliente cliente, Profissional profissional,
                               Servico servico, LocalDateTime inicio) {
        Agendamento novo = new Agendamento(proximoId, cliente, profissional, servico, inicio);
        validarHorario(novo, profissional, inicio, novo.getDataHoraFim(), null);
        proximoId++;
        novo.confirmar();
        agendamentos.add(novo);
        return novo;
    }

    public void cancelar(Agendamento agendamento) {
        if (!agendamentos.contains(agendamento)) {
            throw new IllegalArgumentException("Agendamento não existe (RN08).");
        }
        agendamento.cancelar();
    }

    public void reagendar(Agendamento agendamento, LocalDateTime novoInicio) {
        LocalDateTime novoFim = novoInicio.plusMinutes(agendamento.getServico().getDuracaoMinutos());
        validarHorario(agendamento, agendamento.getProfissional(), novoInicio, novoFim, agendamento);
        agendamento.reagendar(novoInicio);
    }

    public List<Agendamento> listar() {
        return agendamentos;
    }

    private void validarHorario(Agendamento atual, Profissional profissional,
                                LocalDateTime inicio, LocalDateTime fim, Agendamento ignorar) {
        // RN03/RN06: precisa estar na agenda do profissional
        DiaSemana dia = converterDia(inicio.getDayOfWeek());
        boolean dentroDaAgenda = false;
        for (Disponibilidade d : profissional.getDisponibilidades()) {
            if (d.contem(dia, inicio.toLocalTime(), fim.toLocalTime())) {
                dentroDaAgenda = true;
                break;
            }
        }
        if (!dentroDaAgenda) {
            throw new IllegalArgumentException("Horário fora da disponibilidade do profissional (RN03/RN06).");
        }

        // RN04/RN10: não pode conflitar com outro agendamento
        for (Agendamento outro : agendamentos) {
            if (outro != ignorar
                    && outro.getProfissional() == profissional
                    && outro.conflitaCom(inicio, fim)) {
                throw new IllegalArgumentException("Horário já reservado para este profissional (RN04).");
            }
        }
    }

    private DiaSemana converterDia(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> DiaSemana.SEGUNDA;
            case TUESDAY -> DiaSemana.TERCA;
            case WEDNESDAY -> DiaSemana.QUARTA;
            case THURSDAY -> DiaSemana.QUINTA;
            case FRIDAY -> DiaSemana.SEXTA;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
