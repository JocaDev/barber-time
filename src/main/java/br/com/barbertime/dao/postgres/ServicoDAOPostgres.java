package br.com.barbertime.dao.postgres;

import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.exception.PersistenciaException;
import br.com.barbertime.infra.ConnectionSingleton;
import br.com.barbertime.model.Barbearia;
import br.com.barbertime.model.Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicoDAOPostgres implements ServicoDAO {

    private final Barbearia barbearia;

    public ServicoDAOPostgres(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public Servico inserir(Servico s) {
        String sql = "INSERT INTO servico (barbearia_id, nome, descricao, preco, duracao_minutos) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setLong(1, barbearia.getId());
            stmt.setString(2, s.getNome());
            stmt.setString(3, s.getDescricao());
            stmt.setBigDecimal(4, s.getPreco());
            stmt.setInt(5, s.getDuracaoMinutos());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                s.setId(rs.getLong(1));
            }
            s.setBarbearia(barbearia);
            return s;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir serviço", e);
        }
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        try (PreparedStatement stmt = conexao().prepareStatement("SELECT * FROM servico WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar serviço", e);
        }
    }

    @Override
    public List<Servico> listar() {
        try (PreparedStatement stmt = conexao().prepareStatement("SELECT * FROM servico ORDER BY id");
             ResultSet rs = stmt.executeQuery()) {
            List<Servico> saida = new ArrayList<>();
            while (rs.next()) {
                saida.add(mapear(rs));
            }
            return saida;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar serviços", e);
        }
    }

    private Servico mapear(ResultSet rs) throws SQLException {
        Servico s = new Servico(rs.getLong("id"), rs.getString("nome"), rs.getString("descricao"),
                rs.getBigDecimal("preco"), rs.getInt("duracao_minutos"));
        s.setBarbearia(barbearia);
        return s;
    }

    private Connection conexao() throws SQLException {
        return ConnectionSingleton.getConnection();
    }
}
