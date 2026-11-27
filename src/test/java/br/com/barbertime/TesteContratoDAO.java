package br.com.barbertime;

import br.com.barbertime.dao.AgendamentoDAO;
import br.com.barbertime.dao.FabricaDAO;
import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.dao.UsuarioDAO;
import br.com.barbertime.infra.DadosIniciais;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Barbearia;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.DiaSemana;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;
import br.com.barbertime.model.StatusAgendamento;
import br.com.barbertime.model.Usuario;
import br.com.barbertime.service.AutenticacaoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public class TesteContratoDAO {

    private static int ok = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        Barbearia barbearia = DadosIniciais.criarBarbearia();
        FabricaDAO fabrica = FabricaDAO.doAmbiente(barbearia);
        System.out.println("Persistência testada: "
                + System.getenv().getOrDefault("PERSISTENCIA", "memoria")
                + " (" + fabrica.getClass().getSimpleName() + ")");

        // cria os dados de exemplo (idempotente) antes de testar
        DadosIniciais.criar(new CodecDeTeste(), fabrica, barbearia);

        try {
            testarUsuario(fabrica.usuarioDAO());
            testarProfissionalEServico(fabrica.profissionalDAO(), fabrica.servicoDAO(), barbearia);
            testarAgendamento(fabrica);
        } catch (RuntimeException e) {
            falhas++;
            System.out.println("  FALHA exceção inesperada: " + e);
            e.printStackTrace();
        }

        System.out.println("\n" + ok + " verificações OK, " + falhas + " falhas.");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static Cliente clienteTeste;
    private static Profissional profissionalTeste;
    private static Servico servicoTeste;

    private static void testarUsuario(UsuarioDAO dao) {
        System.out.println("\n== UsuarioDAO");
        String email = "teste-dao-" + System.nanoTime() + "@email.com";
        Usuario salvo = dao.inserir(new Cliente(null, "TESTE-DAO", email,
                AutenticacaoService.hash("segredo"), "71900000000"));
        verificar(salvo.getId() != null, "inserir gera id");
        clienteTeste = (Cliente) salvo;

        Optional<Usuario> porId = dao.buscarPorId(salvo.getId());
        verificar(porId.isPresent() && porId.get() instanceof Cliente, "buscarPorId devolve um Cliente");
        verificar(porId.get().getEmail().equals(email), "e-mail preservado");
        verificar(porId.get().autenticar(email, AutenticacaoService.hash("segredo")),
                "hash da senha preservado (login confere)");
        verificar(dao.buscarPorEmail(email.toUpperCase()).isPresent(),
                "buscarPorEmail ignora maiúsculas/minúsculas");
        verificar(dao.buscarPorEmail("nao-existe@x.com").isEmpty(), "e-mail inexistente -> vazio");
        verificar(dao.buscarPorId(987654321L).isEmpty(), "id inexistente -> vazio");

        boolean recusouDuplicado = false;
        try {
            dao.inserir(new Cliente(null, "Outro", email, AutenticacaoService.hash("x"), "1"));
        } catch (IllegalArgumentException e) {
            recusouDuplicado = true;
        }
        verificar(recusouDuplicado, "e-mail repetido é recusado (RN01)");

        Optional<Usuario> admin = dao.buscarPorEmail("admin@barbertime.com");
        verificar(admin.isPresent() && admin.get().getPerfil().name().equals("ADMINISTRADOR"),
                "administrador dos dados iniciais existe");
    }

    private static void testarProfissionalEServico(ProfissionalDAO profissionais, ServicoDAO servicos,
                                                   Barbearia barbearia) {
        System.out.println("\n== ProfissionalDAO e ServicoDAO");
        Profissional p = new Profissional(null, "TESTE-DAO Barbeiro", "Corte", "71911111111");
        p.adicionarDisponibilidade(new Disponibilidade(DiaSemana.SEGUNDA, LocalTime.of(9, 0), LocalTime.of(12, 0)));
        p.adicionarDisponibilidade(new Disponibilidade(DiaSemana.QUARTA, LocalTime.of(14, 0), LocalTime.of(18, 0)));
        p.setBarbearia(barbearia);
        profissionalTeste = profissionais.inserir(p);
        verificar(profissionalTeste.getId() != null, "profissional ganha id");

        Profissional lido = profissionais.buscarPorId(profissionalTeste.getId()).orElseThrow();
        verificar(lido.getDisponibilidades().size() == 2, "disponibilidades gravadas e lidas (2)");
        verificar(lido.getBarbearia() != null && lido.getBarbearia().getId() == 1L, "vinculado à barbearia 1 (RN13)");
        verificar(lido.isAtivo(), "profissional nasce ativo");
        verificar(profissionais.listar().stream().anyMatch(x -> x.getId().equals(lido.getId())),
                "listar inclui o novo profissional");
        verificar(profissionais.buscarPorId(987654321L).isEmpty(), "profissional inexistente -> vazio");

        Servico s = new Servico(null, "TESTE-DAO Serviço", "descrição", new BigDecimal("42.50"), 25);
        s.setBarbearia(barbearia);
        servicoTeste = servicos.inserir(s);
        Servico servicoLido = servicos.buscarPorId(servicoTeste.getId()).orElseThrow();
        verificar(servicoLido.getPreco().compareTo(new BigDecimal("42.50")) == 0, "preço preservado");
        verificar(servicoLido.getDuracaoMinutos() == 25, "duração preservada");
        verificar(servicos.listar().size() >= 4, "listar serviços (3 iniciais + teste)");
    }

    private static void testarAgendamento(FabricaDAO fabrica) {
        System.out.println("\n== AgendamentoDAO");
        AgendamentoDAO dao = fabrica.agendamentoDAO();
        LocalDateTime inicio = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Agendamento a = new Agendamento(null, clienteTeste, profissionalTeste, servicoTeste, inicio);
        a.confirmar();
        dao.inserir(a);
        verificar(a.getId() != null, "inserir gera id");

        Agendamento lido = dao.buscarPorId(a.getId()).orElseThrow();
        verificar(lido.getStatus() == StatusAgendamento.CONFIRMADO, "status gravado");
        verificar(lido.getDataHoraInicio().equals(inicio), "início gravado");
        verificar(lido.getDataHoraFim().equals(inicio.plusMinutes(25)), "fim gravado");
        verificar(a.getCodigoConfirmacao().equals(lido.getCodigoConfirmacao()), "código de confirmação gravado");
        verificar(lido.getCliente().getId().equals(clienteTeste.getId()), "cliente vinculado");
        verificar(lido.getProfissional().getId().equals(profissionalTeste.getId()), "profissional vinculado");
        verificar(lido.getServico().getId().equals(servicoTeste.getId()), "serviço vinculado");

        
        lido.reagendar(inicio.plusHours(2));
        dao.atualizar(lido);
        Agendamento depoisReagendar = dao.buscarPorId(a.getId()).orElseThrow();
        verificar(depoisReagendar.getDataHoraInicio().equals(inicio.plusHours(2)), "atualizar grava o novo horário");
        lido.cancelar();
        dao.atualizar(lido);
        verificar(dao.buscarPorId(a.getId()).orElseThrow().getStatus() == StatusAgendamento.CANCELADO,
                "atualizar grava o status CANCELADO");

        
        List<Agendamento> doCliente = dao.listar(clienteTeste.getId(), null, null);
        verificar(doCliente.size() == 1, "listar por cliente (1)");
        verificar(dao.listar(clienteTeste.getId(), profissionalTeste.getId(), StatusAgendamento.CANCELADO).size() == 1,
                "listar por cliente + profissional + status");
        verificar(dao.listar(clienteTeste.getId(), null, StatusAgendamento.CONFIRMADO).isEmpty(),
                "filtro de status sem resultado -> lista vazia");
        verificar(dao.listarPorProfissional(profissionalTeste.getId()).size() == 1, "listarPorProfissional");
        verificar(dao.listar(null, null, null).stream().anyMatch(x -> x.getId().equals(a.getId())),
                "listar sem filtros inclui o agendamento");

        
        dao.remover(a.getId());
        verificar(dao.buscarPorId(a.getId()).isEmpty(), "remover apaga o registro");
        dao.remover(987654321L);
        verificar(true, "remover id inexistente não lança exceção");
    }

    private static void verificar(boolean condicao, String descricao) {
        if (condicao) {
            ok++;
            System.out.println("  OK    " + descricao);
        } else {
            falhas++;
            System.out.println("  FALHA " + descricao);
        }
    }
}
