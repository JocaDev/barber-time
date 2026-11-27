package br.com.barbertime.command;

import br.com.barbertime.infra.JsonCodec;
import br.com.barbertime.model.StatusAgendamento;
import br.com.barbertime.model.Usuario;

import java.util.Map;


public class Requisicao {

    private final String metodo;
    private final Map<String, String> parametrosCaminho;
    private final Map<String, String> query;
    private final String corpo;
    private final JsonCodec codec;
    private Usuario usuario; // preenchido pelo Roteador depois da autenticação

    public Requisicao(String metodo, Map<String, String> parametrosCaminho,
                      Map<String, String> query, String corpo, JsonCodec codec) {
        this.metodo = metodo;
        this.parametrosCaminho = parametrosCaminho;
        this.query = query;
        this.corpo = corpo;
        this.codec = codec;
    }

    public String getMetodo() { return metodo; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public <T> T corpo(Class<T> tipo) {
        return codec.ler(corpo, tipo);
    }

    public Long idDoCaminho() {
        String valor = parametrosCaminho.get("id");
        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O id '" + valor + "' não é um número válido.");
        }
    }

    public Long queryLong(String nome) {
        String valor = query.get(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O parâmetro " + nome + " deve ser numérico.");
        }
    }

    public StatusAgendamento queryStatus(String nome) {
        String valor = query.get(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return StatusAgendamento.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Status inválido. Use CONFIRMADO, CANCELADO ou CONCLUIDO.");
        }
    }
}
