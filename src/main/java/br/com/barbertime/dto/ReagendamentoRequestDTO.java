package br.com.barbertime.dto;

import java.time.LocalDateTime;

public class ReagendamentoRequestDTO {

    private LocalDateTime novaDataHora;

    public ReagendamentoRequestDTO() {
    }

    public LocalDateTime getNovaDataHora() {
        return novaDataHora;
    }

    public void setNovaDataHora(LocalDateTime novaDataHora) {
        this.novaDataHora = novaDataHora;
    }
}
