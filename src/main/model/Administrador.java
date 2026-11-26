package model;

public class Administrador extends Usuario {

    public Administrador() {
        setPerfil(Perfil.ADMINISTRADOR);
    }

    public Administrador(Long id, String nome, String email, String senhaHash, String telefone) {
        super(id, nome, email, senhaHash, telefone, Perfil.ADMINISTRADOR);
    }
}
