package br.com.barbertime.command;

import br.com.barbertime.model.Perfil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Rota {

    private final String metodo;
    private final String[] partes;
    private final boolean publica;
    private final List<Perfil> perfisPermitidos; // vazio = qualquer usuário logado
    private final Command command;

    Rota(String metodo, String padrao, boolean publica, List<Perfil> perfisPermitidos,
         Command command) {
        this.metodo = metodo;
        this.partes = dividir(padrao);
        this.publica = publica;
        this.perfisPermitidos = perfisPermitidos;
        this.command = command;
    }

    String getMetodo() { return metodo; }
    boolean isPublica() { return publica; }
    List<Perfil> getPerfisPermitidos() { return perfisPermitidos; }
    Command getCommand() { return command; }

    /** Se o caminho casa com o padrão, devolve os parâmetros ({id}); senão, null. */
    Map<String, String> casar(String caminho) {
        String[] recebido = dividir(caminho);
        if (recebido.length != partes.length) {
            return null;
        }
        Map<String, String> parametros = new HashMap<>();
        for (int i = 0; i < partes.length; i++) {
            if (partes[i].startsWith("{") && partes[i].endsWith("}")) {
                parametros.put(partes[i].substring(1, partes[i].length() - 1), recebido[i]);
            } else if (!partes[i].equals(recebido[i])) {
                return null;
            }
        }
        return parametros;
    }

    private static String[] dividir(String caminho) {
        String limpo = caminho == null ? "" : caminho.trim();
        while (limpo.startsWith("/")) {
            limpo = limpo.substring(1);
        }
        while (limpo.endsWith("/")) {
            limpo = limpo.substring(0, limpo.length() - 1);
        }
        return limpo.isEmpty() ? new String[0] : limpo.split("/");
    }
}
