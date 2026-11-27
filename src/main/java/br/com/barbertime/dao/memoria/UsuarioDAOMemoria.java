package br.com.barbertime.dao.memoria;

import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioDAOMemoria implements UsuarioDAO {

    private final List<Usuario> usuarios = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public synchronized Usuario inserir(Usuario usuario) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                throw new IllegalArgumentException("Já existe um usuário com este e-mail (RN01).");
            }
        }
        usuario.setId(proximoId++);
        usuarios.add(usuario);
        return usuario;
    }

    @Override
    public synchronized Optional<Usuario> buscarPorId(Long id) {
        return usuarios.stream().filter(u -> Objects.equals(u.getId(), id)).findFirst();
    }

    @Override
    public synchronized Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }
}
