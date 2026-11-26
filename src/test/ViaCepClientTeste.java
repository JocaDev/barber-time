import client.ViaCepClient;
import dto.ViaCepResponseDTO;

/** Teste isolado do ViaCEP: parte offline (JSON) e parte online (requisição real). */
public class ViaCepClientTeste {

    public static void main(String[] args) throws Exception {
        testarConversaoJson();
        testarCepInexistente();
        testarRequisicaoReal("48110-000");
        System.out.println("\nTodos os testes finalizados.");
    }

    private static void testarConversaoJson() {
        String json = "{\"cep\":\"48110-000\",\"logradouro\":\"\","
                + "\"complemento\":\"\",\"bairro\":\"\","
                + "\"localidade\":\"Catu\",\"uf\":\"BA\"}";
        ViaCepResponseDTO dto = ViaCepClient.converter(json);

        verificar(!dto.isErro(), "JSON válido não deve marcar erro");
        verificar("48110-000".equals(dto.getCep()), "CEP convertido");
        verificar("Catu".equals(dto.getLocalidade()), "Cidade convertida");
        verificar("BA".equals(dto.getUf()), "UF convertida");
        System.out.println("[OK] Conversão do JSON do ViaCEP");
    }

    private static void testarCepInexistente() {
        ViaCepResponseDTO dto = ViaCepClient.converter("{\"erro\": true}");
        verificar(dto.isErro(), "CEP inexistente deve marcar erro");
        System.out.println("[OK] Tratamento de CEP inexistente");
    }

    private static void testarRequisicaoReal(String cep) {
        try {
            ViaCepResponseDTO dto = new ViaCepClient().consultar(cep);
            verificar(!dto.isErro(), "CEP real deve ser encontrado");
            System.out.println("[OK] Requisição real ao ViaCEP: "
                    + dto.getLogradouro() + ", " + dto.getLocalidade() + "/" + dto.getUf());
        } catch (Exception e) {
            System.out.println("[AVISO] Sem acesso ao ViaCEP agora (internet?): " + e);
        }
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError("FALHOU: " + mensagem);
        }
    }
}
