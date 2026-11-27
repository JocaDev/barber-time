package br.com.barbertime.dao.postgres;

import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.exception.PersistenciaException;
import br.com.barbertime.infra.ConnectionSingleton;
import br.com.barbertime.model.Administrador;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.Perfil;
import br.com.barbertime.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioDAOPostgres implements UsuarioDAO {

    @Override
    public Usuario inserir(Usuario u) {
        String sql = "INSERT INTO usuario (nome, email, senha_hash, telefone, perfil, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getSenhaHash());
            stmt.setString(4, u.getTelefone());
            stmt.setString(5, u.getPerfil().name());
            stmt.setBoolean(6, u.isAtivo());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                u.setId(rs.getLong(1));
            }
            return u;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // unique_violation
                throw new IllegalArgumentException("Já existe um usuário com este e-mail (RN01).");
            }
            throw new PersistenciaException("Erro ao inserir usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return buscar("SELECT * FROM usuario WHERE id = ?", id);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return buscar("SELECT * FROM usuario WHERE LOWER(email) = LOWER(?)", email);
    }

    private Optional<Usuario> buscar(String sql, Object parametro) {
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setObject(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar usuário", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Perfil perfil = Perfil.valueOf(rs.getString("perfil"));
        Usuario u = perfil == Perfil.ADMINISTRADOR ? new Administrador() : new Cliente();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setTelefone(rs.getString("telefone"));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    }

    private Connection conexao() throws SQLException {
        return ConnectionSingleton.getConnection();
    }
}
