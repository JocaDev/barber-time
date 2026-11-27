package br.com.barbertime.dao.postgres;

import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.exception.PersistenciaException;
import br.com.barbertime.infra.ConnectionSingleton;
import br.com.barbertime.model.Barbearia;
import br.com.barbertime.model.DiaSemana;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Profissional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfissionalDAOPostgres implements ProfissionalDAO {

    private final Barbearia barbearia;

    public ProfissionalDAOPostgres(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public Profissional inserir(Profissional p) {
        String sql = "INSERT INTO profissional (nome, especialidade, telefone, ativo, barbearia_id) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getEspecialidade());
            stmt.setString(3, p.getTelefone());
            stmt.setBoolean(4, p.isAtivo());
            stmt.setLong(5, barbearia.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                p.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir profissional", e);
        }

        try {
            for (Disponibilidade d : p.getDisponibilidades()) {
                inserirDisponibilidade(p.getId(), d);
            }
        } catch (SQLException e) {
            apagarProfissional(p.getId()); // desfaz o que já foi gravado (ON DELETE CASCADE)
            throw new PersistenciaException("Erro ao inserir disponibilidade", e);
        }
        p.setBarbearia(barbearia);
        return p;
    }

    @Override
    public Optional<Profissional> buscarPorId(Long id) {
        try (PreparedStatement stmt = conexao().prepareStatement("SELECT * FROM profissional WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar profissional", e);
        }
    }

    @Override
    public List<Profissional> listar() {
        try (PreparedStatement stmt = conexao().prepareStatement("SELECT * FROM profissional ORDER BY id");
             ResultSet rs = stmt.executeQuery()) {
            List<Profissional> saida = new ArrayList<>();
            while (rs.next()) {
                saida.add(mapear(rs));
            }
            return saida;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar profissionais", e);
        }
    }

    private void inserirDisponibilidade(Long profissionalId, Disponibilidade d) throws SQLException {
        String sql = "INSERT INTO disponibilidade (profissional_id, dia_semana, hora_inicio, hora_fim) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setLong(1, profissionalId);
            stmt.setString(2, d.getDiaSemana().name());
            stmt.setTime(3, Time.valueOf(d.getHoraInicio()));
            stmt.setTime(4, Time.valueOf(d.getHoraFim()));
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                d.setId(rs.getLong(1));
            }
        }
    }

    private void apagarProfissional(Long id) {
        try (PreparedStatement stmt = conexao().prepareStatement("DELETE FROM profissional WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException ignorada) {
            // já estamos tratando um erro; o original é o que importa
        }
    }

    private Profissional mapear(ResultSet rs) throws SQLException {
        Profissional p = new Profissional(rs.getLong("id"), rs.getString("nome"),
                rs.getString("especialidade"), rs.getString("telefone"));
        p.setAtivo(rs.getBoolean("ativo"));
        p.setBarbearia(barbearia);

        String sql = "SELECT * FROM disponibilidade WHERE profissional_id = ? ORDER BY id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setLong(1, p.getId());
            try (ResultSet ds = stmt.executeQuery()) {
                while (ds.next()) {
                    Disponibilidade d = new Disponibilidade(
                            DiaSemana.valueOf(ds.getString("dia_semana")),
                            ds.getTime("hora_inicio").toLocalTime(),
                            ds.getTime("hora_fim").toLocalTime());
                    d.setId(ds.getLong("id"));
                    p.adicionarDisponibilidade(d);
                }
            }
        }
        return p;
    }

    private Connection conexao() throws SQLException {
        return ConnectionSingleton.getConnection();
    }
}
