package model;

import java.util.ArrayList;
import java.util.List;

public class Barbearia {

    private Long id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String numeroEndereco;
    private Endereco endereco;
    private Administrador administrador;
    private final List<Profissional> profissionais = new ArrayList<>();
    private final List<Servico> servicos = new ArrayList<>();

    public Barbearia() {
    }

    public Barbearia(Long id, String nome, String cnpj, String telefone,
                     String numeroEndereco, Endereco endereco, Administrador administrador) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.numeroEndereco = numeroEndereco;
        this.endereco = endereco;
        this.administrador = administrador;
    }

    /** RN13: vincula o profissional a esta barbearia. */
    public void adicionarProfissional(Profissional profissional) {
        profissional.setBarbearia(this);
        profissionais.add(profissional);
    }

    public void adicionarServico(Servico servico) {
        servico.setBarbearia(this);
        servicos.add(servico);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getNumeroEndereco() { return numeroEndereco; }
    public void setNumeroEndereco(String numeroEndereco) { this.numeroEndereco = numeroEndereco; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
    public List<Profissional> getProfissionais() { return profissionais; }
    public List<Servico> getServicos() { return servicos; }

    @Override
    public String toString() {
        return "Barbearia{id=" + id + ", nome='" + nome + "'}";
    }
}
