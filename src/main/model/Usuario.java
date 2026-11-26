package model;


public abstract class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;
    private Perfil perfil;
    private boolean ativo = true;

    protected Usuario() {
    }

    protected Usuario(Long id, String nome, String email, String senhaHash, String telefone, Perfil perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.perfil = perfil;
    }


    public boolean autenticar(String email, String senhaHash) {
        return this.ativo
                && this.email != null && this.email.equalsIgnoreCase(email)
                && this.senhaHash != null && this.senhaHash.equals(senhaHash);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}
