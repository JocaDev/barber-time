package br.com.barbertime.service;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.exception.ConflitoException;
import br.com.barbertime.exception.RecursoNaoEncontradoException;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.DiaSemana;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;
import br.com.barbertime.model.StatusAgendamento;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Regras de agendamento (RN03 a RN11). Não sabe onde os dados ficam: só fala com a interface
 * AgendamentoDAO. Os métodos são synchronized porque o Tomcat atende várias requisições ao mesmo tempo.
 */
public class AgendamentoService {

    private final AgendamentoDAO dao;

    public AgendamentoService(AgendamentoDAO dao) {
        this.dao = dao;
    }

    // ---------- CREATE ----------

    public synchronized Agendamento agendar(Cliente cliente, Profissional profissional,
                                            Servico servico, LocalDateTime inicio) {
        if (cliente == null || profissional == null || servico == null || inicio == null) {
            throw new IllegalArgumentException("Cliente, profissional, serviço e horário são obrigatórios.");
        }
        if (!profissional.isAtivo()) {
            throw new IllegalArgumentException("Profissional inativo.");
        }
        if (servico.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("Serviço com duração inválida.");
        }
        validarNaoPassado(inicio);

        Agendamento novo = new Agendamento(null, cliente, profissional, servico, inicio);
        validarHorario(profissional, inicio, novo.getDataHoraFim(), null);

        novo.confirmar();
        return dao.inserir(novo);
    }

    // ---------- READ ----------

    public synchronized Agendamento buscarPorId(Long id) {
        return dao.buscarPorId(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Agendamento " + id + " não encontrado."));
    }

    public synchronized List<Agendamento> listar() {
        return dao.listar(null, null, null);
    }

    /** Qualquer filtro pode ser null (= não filtra). */
    public synchronized List<Agendamento> listar(Long clienteId, Long profissionalId,
                                                 StatusAgendamento status) {
        return dao.listar(clienteId, profissionalId, status);
    }

    // ---------- UPDATE ----------

    public synchronized void reagendar(Agendamento agendamento, LocalDateTime novoInicio) {
        if (novoInicio == null) {
            throw new IllegalArgumentException("A nova data/hora é obrigatória.");
        }
        if (agendamento.getStatus() != StatusAgendamento.CONFIRMADO) {
            throw new IllegalStateException("Só é possível reagendar um agendamento confirmado.");
        }
        validarNaoPassado(novoInicio);

        LocalDateTime novoFim = novoInicio.plusMinutes(agendamento.getServico().getDuracaoMinutos());
        validarHorario(agendamento.getProfissional(), novoInicio, novoFim, agendamento);
        agendamento.reagendar(novoInicio);
        dao.atualizar(agendamento);
    }

    public synchronized void concluir(Agendamento agendamento) {
        agendamento.concluir();
        dao.atualizar(agendamento);
    }

    // ---------- DELETE ----------

    /** Cancelamento: o registro fica, o status vira CANCELADO e o horário é liberado (RN09). */
    public synchronized void cancelar(Agendamento agendamento) {
        if (dao.buscarPorId(agendamento.getId()).isEmpty()) {
            throw new RecursoNaoEncontradoException("Agendamento não existe (RN08).");
        }
        agendamento.cancelar();
        dao.atualizar(agendamento);
    }

    /** Exclusão definitiva (uso administrativo). */
    public synchronized void remover(Long id) {
        buscarPorId(id); // 404 se não existir
        dao.remover(id);
    }

    // ---------- regras internas ----------

    private void validarNaoPassado(LocalDateTime inicio) {
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar em horário que já passou.");
        }
    }

    private void validarHorario(Profissional profissional, LocalDateTime inicio,
                                LocalDateTime fim, Agendamento ignorar) {
        // RN03/RN06: precisa estar dentro da agenda do profissional
        if (!inicio.toLocalDate().equals(fim.toLocalDate())) {
            throw new IllegalArgumentException("O atendimento não pode atravessar a meia-noite.");
        }
        DiaSemana dia = converterDia(inicio.getDayOfWeek());
        boolean dentroDaAgenda = false;
        for (Disponibilidade d : profissional.getDisponibilidades()) {
            if (d.contem(dia, inicio.toLocalTime(), fim.toLocalTime())) {
                dentroDaAgenda = true;
                break;
            }
        }
        if (!dentroDaAgenda) {
            throw new IllegalArgumentException(
                    "Horário fora da disponibilidade do profissional (RN03/RN06).");
        }

        // RN04/RN10: não pode conflitar com outro agendamento confirmado
        for (Agendamento outro : dao.listarPorProfissional(profissional.getId())) {
            boolean ehOMesmo = ignorar != null && Objects.equals(outro.getId(), ignorar.getId());
            if (!ehOMesmo && outro.conflitaCom(inicio, fim)) {
                throw new ConflitoException("Horário já reservado para este profissional (RN04).");
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
