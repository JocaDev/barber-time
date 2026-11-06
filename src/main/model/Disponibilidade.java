package model;

import java.time.LocalTime;

public class Disponibilidade {

    private Long id;
    private Profissional profissional;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Disponibilidade() {
    }

    public Disponibilidade(DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("A hora de início deve ser anterior à hora de fim.");
        }
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

   
    public boolean sobrepoe(Disponibilidade outra) {
        return this.diaSemana == outra.diaSemana
                && this.horaInicio.isBefore(outra.horaFim)
                && outra.horaInicio.isBefore(this.horaFim);
    }

    
    public boolean contem(DiaSemana dia, LocalTime inicio, LocalTime fim) {
        return this.diaSemana == dia
                && !inicio.isBefore(horaInicio)
                && !fim.isAfter(horaFim);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    @Override
    public String toString() {
        return diaSemana + " " + horaInicio + "-" + horaFim;
    }
}
