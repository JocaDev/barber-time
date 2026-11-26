package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Agendamento {

    private Long id;
    private Cliente cliente;
    private Profissional profissional;
    private Servico servico;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAgendamento status;
    private String codigoConfirmacao;
    private LocalDateTime criadoEm;

    public Agendamento() {
    }

    public Agendamento(Long id, Cliente cliente, Profissional profissional,
                       Servico servico, LocalDateTime dataHoraInicio) {
        this.id = id;
        this.cliente = cliente;
        this.profissional = profissional;
        this.servico = servico;
        definirHorario(dataHoraInicio);
        this.criadoEm = LocalDateTime.now();
    }

    private void definirHorario(LocalDateTime inicio) {
        this.dataHoraInicio = inicio;
        this.dataHoraFim = inicio.plusMinutes(servico.getDuracaoMinutos());
    }

    
    public void confirmar() {
        this.status = StatusAgendamento.CONFIRMADO;
        this.codigoConfirmacao = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    
    public void cancelar() {
        if (status != StatusAgendamento.CONFIRMADO) {
            throw new IllegalStateException("Só é possível cancelar um agendamento confirmado (RN08).");
        }
        this.status = StatusAgendamento.CANCELADO;
    }

    
    public void reagendar(LocalDateTime novoInicio) {
        if (status != StatusAgendamento.CONFIRMADO) {
            throw new IllegalStateException("Só é possível reagendar um agendamento confirmado.");
        }
        definirHorario(novoInicio);
    }

   
    public boolean conflitaCom(LocalDateTime inicio, LocalDateTime fim) {
        return status == StatusAgendamento.CONFIRMADO
                && dataHoraInicio.isBefore(fim)
                && inicio.isBefore(dataHoraFim);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public StatusAgendamento getStatus() { return status; }
    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    @Override
    public String toString() {
        return "Agendamento{id=" + id + ", " + servico.getNome() + " com " + profissional.getNome()
                + ", " + dataHoraInicio + ", status=" + status + ", codigo=" + codigoConfirmacao + "}";
    }
}
