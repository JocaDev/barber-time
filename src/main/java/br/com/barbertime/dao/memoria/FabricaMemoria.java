package br.com.barbertime.dao.memoria;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.dao.FabricaDAO;
import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.dao.UsuarioDAO;


public class FabricaMemoria implements FabricaDAO {

    private final UsuarioDAO usuarios = new UsuarioDAOMemoria();
    private final ProfissionalDAO profissionais = new ProfissionalDAOMemoria();
    private final ServicoDAO servicos = new ServicoDAOMemoria();
    private final AgendamentoDAO agendamentos = new AgendamentoDAOMemoria();

    @Override public UsuarioDAO usuarioDAO() { return usuarios; }
    @Override public ProfissionalDAO profissionalDAO() { return profissionais; }
    @Override public ServicoDAO servicoDAO() { return servicos; }
    @Override public AgendamentoDAO agendamentoDAO() { return agendamentos; }
}
