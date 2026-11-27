package br.com.barbertime.servlet;

import br.com.barbertime.command.Resposta;
import br.com.barbertime.command.Roteador;
import br.com.barbertime.infra.JsonCodec;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Padrão Front Controller: TODAS as chamadas a /api/* passam por este servlet.
 * Ele só traduz HTTP <-> Roteador; as regras ficam nos Commands e nos Services.
 */
public class FrontControllerServlet extends HttpServlet {

    private final Roteador roteador;
    private final JsonCodec codec;

    public FrontControllerServlet(Roteador roteador, JsonCodec codec) {
        this.roteador = roteador;
        this.codec = codec;
    }

    // Sobrescrevemos service() para tratar também PATCH (o HttpServlet não tem doPatch).
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String caminho = req.getPathInfo() == null ? "/" : req.getPathInfo();

        Map<String, String> query = new HashMap<>();
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            if (e.getValue().length > 0) {
                query.put(e.getKey(), e.getValue()[0]);
            }
        }

        String corpo = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        Resposta resposta = roteador.processar(
                req.getMethod(), caminho, query, corpo, req.getHeader("Authorization"));

        resp.setStatus(resposta.getStatus());
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (resposta.getCorpo() != null) {
            resp.getWriter().write(codec.escrever(resposta.getCorpo()));
        }
    }
}
