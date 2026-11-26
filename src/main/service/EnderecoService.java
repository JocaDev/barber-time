package service;

import client.ViaCepClient;
import dto.ViaCepResponseDTO;
import model.Endereco;

import java.io.IOException;

/** Busca o CEP no ViaCEP e devolve um Endereco. */
public class EnderecoService {

    private final ViaCepClient viaCepClient;

    public EnderecoService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Endereco buscarPorCep(String cep) throws IOException, InterruptedException {
        ViaCepResponseDTO dto = viaCepClient.consultar(cep);
        return paraEndereco(dto);
    }

    public Endereco paraEndereco(ViaCepResponseDTO dto) {
        if (dto.isErro()) {
            throw new IllegalArgumentException("CEP não encontrado.");
        }
        return new Endereco(dto.getCep(), dto.getLogradouro(), dto.getComplemento(),
                dto.getBairro(), dto.getLocalidade(), dto.getUf());
    }
}
