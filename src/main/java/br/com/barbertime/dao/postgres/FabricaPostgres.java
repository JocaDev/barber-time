package br.com.barbertime.dao.postgres;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.dao.FabricaDAO;
import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.model.Barbearia;


public class FabricaPostgres implements FabricaDAO {

    private final UsuarioDAO usuarios = new UsuarioDAOPostgres();
    private final ProfissionalDAO profissionais;
    private final ServicoDAO servicos;
    private final AgendamentoDAO agendamentos;

    public FabricaPostgres(Barbearia barbearia) {
        this.profissionais = new ProfissionalDAOPostgres(barbearia);
        this.servicos = new ServicoDAOPostgres(barbearia);
        this.agendamentos = new AgendamentoDAOPostgres(usuarios, profissionais, servicos);
    }

    @Override public UsuarioDAO usuarioDAO() { return usuarios; }
    @Override public ProfissionalDAO profissionalDAO() { return profissionais; }
    @Override public ServicoDAO servicoDAO() { return servicos; }
    @Override public AgendamentoDAO agendamentoDAO() { return agendamentos; }
}
