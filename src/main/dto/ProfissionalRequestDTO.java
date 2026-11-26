import java.util.List;

public class ProfissionalRequestDTO {

    private String nome;
    private String especialidade;
    private String telefone;
    private Long barbeariaId;
    private List<DisponibilidadeDTO> disponibilidades;

    public ProfissionalRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(Long barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public List<DisponibilidadeDTO> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<DisponibilidadeDTO> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }
}
