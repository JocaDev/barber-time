package br.com.barbertime.dao;

import br.com.barbertime.infra.ConnectionSingleton;
import br.com.barbertime.model.Agendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AgendamentoDAO {

    public void criar(Agendamento agendamento) throws SQLException {

        String sql = """
            INSERT INTO agendamento
            (cliente_id, profissional_id, servico_id,
             data_hora_inicio, data_hora_fim, status,
             codigo_confirmacao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        Connection connection = ConnectionSingleton.getConnection();

        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setLong(1, agendamento.getCliente().getId());
        stmt.setLong(2, agendamento.getProfissional().getId());
        stmt.setLong(3, agendamento.getServico().getId());
        stmt.setTimestamp(4,
                java.sql.Timestamp.valueOf(agendamento.getDataHoraInicio()));
        stmt.setTimestamp(5,
                java.sql.Timestamp.valueOf(agendamento.getDataHoraFim()));
        stmt.setString(6, agendamento.getStatus().name());
        stmt.setString(7, agendamento.getCodigoConfirmacao());

        stmt.executeUpdate();

        stmt.close();
    }
}