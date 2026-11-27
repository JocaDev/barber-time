package br.com.barbertime.dao;

import br.com.barbertime.model.Usuario;

import java.util.Optional;


public interface UsuarioDAO {

    
    Usuario inserir(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    
    Optional<Usuario> buscarPorEmail(String email);
}
