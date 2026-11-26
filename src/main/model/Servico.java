package model;

import java.math.BigDecimal;

public class Servico {

    private Long id;
    private Barbearia barbearia;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int duracaoMinutos;

    public Servico() {
    }

    public Servico(Long id, String nome, String descricao, BigDecimal preco, int duracaoMinutos) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Barbearia getBarbearia() { return barbearia; }
    public void setBarbearia(Barbearia barbearia) { this.barbearia = barbearia; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    @Override
    public String toString() {
        return nome + " (R$ " + preco + ", " + duracaoMinutos + " min)";
    }
}
