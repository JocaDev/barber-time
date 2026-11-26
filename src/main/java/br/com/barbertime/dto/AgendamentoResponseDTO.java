package br.com.barbertime.dto;

import br.com.barbertime.model.StatusAgendamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgendamentoResponseDTO {

    private Long id;
    private String codigoConfirmacao;
    private StatusAgendamento status;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private LocalDateTime criadoEm;
    private Long clienteId;
    private String clienteNome;
    private Long profissionalId;
    private String profissionalNome;
    private Long servicoId;
    private String servicoNome;
    private BigDecimal servicoPreco;
    private String barbeariaNome;

    public AgendamentoResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }

    public void setCodigoConfirmacao(String codigoConfirmacao) {
        this.codigoConfirmacao = codigoConfirmacao;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public Long getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(Long profissionalId) {
        this.profissionalId = profissionalId;
    }

    public String getProfissionalNome() {
        return profissionalNome;
    }

    public void setProfissionalNome(String profissionalNome) {
        this.profissionalNome = profissionalNome;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public String getServicoNome() {
        return servicoNome;
    }

    public void setServicoNome(String servicoNome) {
        this.servicoNome = servicoNome;
    }

    public BigDecimal getServicoPreco() {
        return servicoPreco;
    }

    public void setServicoPreco(BigDecimal servicoPreco) {
        this.servicoPreco = servicoPreco;
    }

    public String getBarbeariaNome() {
        return barbeariaNome;
    }

    public void setBarbeariaNome(String barbeariaNome) {
        this.barbeariaNome = barbeariaNome;
    }
}
