package model;

import java.util.ArrayList;
import java.util.List;

public class Profissional {

    private Long id;
    private String nome;
    private String especialidade;
    private String telefone;
    private boolean ativo = true;
    private Barbearia barbearia;
    private final List<Disponibilidade> disponibilidades = new ArrayList<>();

    public Profissional() {
    }

    public Profissional(Long id, String nome, String especialidade, String telefone) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.telefone = telefone;
    }

    
    public void adicionarDisponibilidade(Disponibilidade nova) {
        for (Disponibilidade existente : disponibilidades) {
            if (existente.sobrepoe(nova)) {
                throw new IllegalArgumentException(
                        "Horário sobreposto com uma disponibilidade já cadastrada (RN07).");
            }
        }
        nova.setProfissional(this);
        disponibilidades.add(nova);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Barbearia getBarbearia() { return barbearia; }
    public void setBarbearia(Barbearia barbearia) { this.barbearia = barbearia; }
    public List<Disponibilidade> getDisponibilidades() { return disponibilidades; }

    @Override
    public String toString() {
        return "Profissional{id=" + id + ", nome='" + nome + "'}";
    }
}
