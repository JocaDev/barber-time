package br.com.barbertime.infra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


public class GsonCodec implements JsonCodec {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter().nullSafe())
            .setPrettyPrinting()
            .create();

    @Override
    public <T> T ler(String json, Class<T> tipo) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Corpo da requisição vazio: envie um JSON.");
        }
        try {
            T objeto = gson.fromJson(json, tipo);
            if (objeto == null) {
                throw new IllegalArgumentException("Corpo da requisição vazio: envie um JSON.");
            }
            return objeto;
        } catch (JsonParseException | DateTimeParseException e) {
            throw new IllegalArgumentException("JSON inválido: " + e.getMessage());
        }
    }

    @Override
    public String escrever(Object objeto) {
        return gson.toJson(objeto);
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime valor) throws IOException {
            out.value(valor.toString());
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString());
        }
    }

    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        @Override
        public void write(JsonWriter out, LocalTime valor) throws IOException {
            out.value(valor.toString());
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            return LocalTime.parse(in.nextString());
        }
    }
}
