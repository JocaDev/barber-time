package br.com.barbertime.dao.postgres;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.exception.PersistenciaException;
import br.com.barbertime.infra.ConnectionSingleton;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.StatusAgendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendamentoDAOPostgres implements AgendamentoDAO {

    private final UsuarioDAO usuarios;
    private final ProfissionalDAO profissionais;
    private final ServicoDAO servicos;

    public AgendamentoDAOPostgres(UsuarioDAO usuarios, ProfissionalDAO profissionais,
                                  ServicoDAO servicos) {
        this.usuarios = usuarios;
        this.profissionais = profissionais;
        this.servicos = servicos;
    }

    @Override
    public Agendamento inserir(Agendamento a) {
        String sql = "INSERT INTO agendamento (cliente_id, profissional_id, servico_id, "
                + "data_hora_inicio, data_hora_fim, status, codigo_confirmacao, criado_em) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setLong(1, a.getCliente().getId());
            stmt.setLong(2, a.getProfissional().getId());
            stmt.setLong(3, a.getServico().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(a.getDataHoraInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(a.getDataHoraFim()));
            stmt.setString(6, a.getStatus().name());
            stmt.setString(7, a.getCodigoConfirmacao());
            stmt.setTimestamp(8, Timestamp.valueOf(a.getCriadoEm()));
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                a.setId(rs.getLong(1));
            }
            return a;
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao inserir agendamento", e);
        }
    }

    @Override
    public void atualizar(Agendamento a) {
        String sql = "UPDATE agendamento SET data_hora_inicio = ?, data_hora_fim = ?, "
                + "status = ?, codigo_confirmacao = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao().prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(a.getDataHoraInicio()));
            stmt.setTimestamp(2, Timestamp.valueOf(a.getDataHoraFim()));
            stmt.setString(3, a.getStatus().name());
            stmt.setString(4, a.getCodigoConfirmacao());
            stmt.setLong(5, a.getId());
            if (stmt.executeUpdate() == 0) {
                throw new IllegalStateException("Agendamento " + a.getId() + " não existe.");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao atualizar agendamento", e);
        }
    }

    @Override
    public Optional<Agendamento> buscarPorId(Long id) {
        try (PreparedStatement stmt = conexao().prepareStatement("SELECT * FROM agendamento WHERE id = ?")) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar agendamento", e);
        }
    }

    @Override
    public List<Agendamento> listar(Long clienteId, Long profissionalId, StatusAgendamento status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM agendamento WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (clienteId != null) {
            sql.append(" AND cliente_id = ?");
            parametros.add(clienteId);
        }
        if (profissionalId != null) {
            sql.append(" AND profissional_id = ?");
            parametros.add(profissionalId);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            parametros.add(status.name());
        }
        sql.append(" ORDER BY data_hora_inicio, id");

        try (PreparedStatement stmt = conexao().prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Agendamento> saida = new ArrayList<>();
                while (rs.next()) {
                    saida.add(mapear(rs));
                }
                return saida;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar agendamentos", e);
        }
    }

    @Override
    public List<Agendamento> listarPorProfissional(Long profissionalId) {
        return listar(null, profissionalId, null);
    }

    @Override
    public void remover(Long id) {
        try (PreparedStatement stmt = conexao().prepareStatement("DELETE FROM agendamento WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao remover agendamento", e);
        }
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getLong("id"));
        a.setCliente((Cliente) usuarios.buscarPorId(rs.getLong("cliente_id")).orElseThrow());
        a.setProfissional(profissionais.buscarPorId(rs.getLong("profissional_id")).orElseThrow());
        a.setServico(servicos.buscarPorId(rs.getLong("servico_id")).orElseThrow());
        a.setDataHoraInicio(rs.getTimestamp("data_hora_inicio").toLocalDateTime());
        a.setDataHoraFim(rs.getTimestamp("data_hora_fim").toLocalDateTime());
        a.setStatus(StatusAgendamento.valueOf(rs.getString("status")));
        a.setCodigoConfirmacao(rs.getString("codigo_confirmacao"));
        a.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return a;
    }

    private Connection conexao() throws SQLException {
        return ConnectionSingleton.getConnection();
    }
}
