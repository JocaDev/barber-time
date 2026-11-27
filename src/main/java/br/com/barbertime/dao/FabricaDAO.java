package br.com.barbertime.dao;

import br.com.barbertime.dao.memoria.FabricaMemoria;
import br.com.barbertime.dao.postgres.FabricaPostgres;
import br.com.barbertime.model.Barbearia;


public interface FabricaDAO {

    UsuarioDAO usuarioDAO();

    ProfissionalDAO profissionalDAO();

    ServicoDAO servicoDAO();

    AgendamentoDAO agendamentoDAO();

    static FabricaDAO doAmbiente(Barbearia barbearia) {
        String tipo = System.getenv().getOrDefault("PERSISTENCIA", "memoria").trim().toLowerCase();
        switch (tipo) {
            case "memoria":
                return new FabricaMemoria();
            case "postgres":
                return new FabricaPostgres(barbearia);
            default:
                throw new IllegalArgumentException(
                        "PERSISTENCIA inválida: '" + tipo + "'. Use memoria ou postgres.");
        }
    }
}
