package model;

public class Cliente extends Usuario {

    public Cliente() {
        setPerfil(Perfil.CLIENTE);
    }

    public Cliente(Long id, String nome, String email, String senhaHash, String telefone) {
        super(id, nome, email, senhaHash, telefone, Perfil.CLIENTE);
    }
}
