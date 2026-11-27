package br.com.barbertime;

import br.com.barbertime.infra.JsonCodec;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Codec mínimo SÓ para os testes rodarem sem baixar o Gson: lê JSON plano
 * ({"campo": "texto" | número | null}) para os DTOs usados nos testes de agendamento.
 */
class CodecDeTeste implements JsonCodec {

    private static final Pattern PAR = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*(\"([^\"]*)\"|-?\\d+(?:\\.\\d+)?|null|true|false)");

    @Override
    public <T> T ler(String json, Class<T> tipo) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Corpo da requisição vazio: envie um JSON.");
        }
        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            throw new IllegalArgumentException("JSON inválido.");
        }
        Map<String, String> campos = new HashMap<>();
        Matcher m = PAR.matcher(json);
        while (m.find()) {
            campos.put(m.group(1), m.group(3) != null ? m.group(3) : m.group(2));
        }
        try {
            T objeto = tipo.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, String> e : campos.entrySet()) {
                Field f;
                try {
                    f = tipo.getDeclaredField(e.getKey());
                } catch (NoSuchFieldException ignorado) {
                    continue;
                }
                f.setAccessible(true);
                String v = e.getValue();
                if (v.equals("null")) {
                    continue;
                }
                if (f.getType() == String.class) {
                    f.set(objeto, v);
                } else if (f.getType() == Long.class) {
                    f.set(objeto, Long.valueOf(v));
                } else if (f.getType() == LocalDateTime.class) {
                    f.set(objeto, LocalDateTime.parse(v));
                }
            }
            return objeto;
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException("JSON inválido: " + e.getMessage());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String escrever(Object objeto) {
        return String.valueOf(objeto);
    }
}
