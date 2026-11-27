package br.com.barbertime.command;

import java.util.Map;


public class Resposta {

    private final int status;
    private final Object corpo;

    public Resposta(int status, Object corpo) {
        this.status = status;
        this.corpo = corpo;
    }

    public static Resposta ok(Object corpo) {
        return new Resposta(200, corpo);
    }

    public static Resposta criado(Object corpo) {
        return new Resposta(201, corpo);
    }

    public static Resposta semConteudo() {
        return new Resposta(204, null);
    }

    public static Resposta erro(int status, String mensagem) {
        return new Resposta(status, Map.of("erro", mensagem));
    }

    public int getStatus() { return status; }
    public Object getCorpo() { return corpo; }
}
