package br.com.barbertime;

import br.com.barbertime.infra.ConnectionSingleton;
import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        try {
            Connection connection = ConnectionSingleton.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexão com PostgreSQL realizada com sucesso!");
            }

        } catch (Exception e) {
            System.out.println("Erro ao conectar:");
            e.printStackTrace();
        }
    }
}