package client;

import dto.ViaCepResponseDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Cliente HTTP do ViaCEP (usa só o JDK). */
public class ViaCepClient {

    private static final String URL_BASE = "https://viacep.com.br/ws/%s/json/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** Consulta o CEP (8 dígitos, com ou sem hífen). */
    public ViaCepResponseDTO consultar(String cep) throws IOException, InterruptedException {
        String cepLimpo = cep == null ? "" : cep.replaceAll("\\D", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL_BASE, cepLimpo)))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("ViaCEP respondeu com status " + response.statusCode());
        }
        return converter(response.body());
    }

    /** Converte o JSON em DTO. Público para poder testar. */
    public static ViaCepResponseDTO converter(String json) {
        ViaCepResponseDTO dto = new ViaCepResponseDTO();
        // CEP inexistente retorna {"erro": true}
        if (Pattern.compile("\"erro\"\\s*:\\s*\"?true\"?").matcher(json).find()) {
            dto.setErro(true);
            return dto;
        }
        dto.setCep(campo(json, "cep"));
        dto.setLogradouro(campo(json, "logradouro"));
        dto.setComplemento(campo(json, "complemento"));
        dto.setBairro(campo(json, "bairro"));
        dto.setLocalidade(campo(json, "localidade"));
        dto.setUf(campo(json, "uf"));
        return dto;
    }

    private static String campo(String json, String nome) {
        Matcher m = Pattern.compile("\"" + nome + "\"\\s*:\\s*\"(.*?)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }
}
