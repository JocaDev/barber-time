package br.com.barbertime.infra;

import br.com.barbertime.command.ContextoAplicacao;
import br.com.barbertime.dao.FabricaDAO;
import br.com.barbertime.model.Administrador;
import br.com.barbertime.model.Barbearia;
import br.com.barbertime.model.Cliente;
import br.com.barbertime.model.DiaSemana;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Endereco;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;
import br.com.barbertime.service.AgendamentoService;
import br.com.barbertime.service.AutenticacaoService;
import br.com.barbertime.service.CatalogoService;

import java.math.BigDecimal;
import java.time.LocalTime;

/*
 *   Logins:
 *   admin@barbertime.com / admin123   (ADMINISTRADOR)
 *   maria@email.com      / maria123   (CLIENTE)
 *   carlos@email.com     / carlos123  (CLIENTE)
 */
public final class DadosIniciais {

    private DadosIniciais() {
    }

    
    public static ContextoAplicacao criar(JsonCodec codec) {
        Barbearia barbearia = criarBarbearia();
        return criar(codec, FabricaDAO.doAmbiente(barbearia), barbearia);
    }

    public static ContextoAplicacao criar(JsonCodec codec, FabricaDAO fabrica, Barbearia barbearia) {
        semear(fabrica, barbearia);

        AutenticacaoService auth = new AutenticacaoService(fabrica.usuarioDAO());
        CatalogoService catalogo = new CatalogoService(
                barbearia, fabrica.profissionalDAO(), fabrica.servicoDAO());
        AgendamentoService agendamentos = new AgendamentoService(fabrica.agendamentoDAO());
        return new ContextoAplicacao(auth, catalogo, agendamentos, codec);
    }

    
    public static Barbearia criarBarbearia() {
        Endereco endereco = new Endereco("48110-000", "", "", "", "Catu", "BA");
        return new Barbearia(1L, "Barbearia Exemplo", "00.000.000/0001-00",
                "7133330000", "100", endereco, null);
    }

    private static void semear(FabricaDAO fabrica, Barbearia barbearia) {
        if (fabrica.usuarioDAO().buscarPorEmail("admin@barbertime.com").isEmpty()) {
            Administrador admin = (Administrador) fabrica.usuarioDAO().inserir(
                    new Administrador(null, "Admin", "admin@barbertime.com",
                            AutenticacaoService.hash("admin123"), "71999990000"));
            barbearia.setAdministrador(admin);
            fabrica.usuarioDAO().inserir(new Cliente(null, "Maria", "maria@email.com",
                    AutenticacaoService.hash("maria123"), "71977770000"));
            fabrica.usuarioDAO().inserir(new Cliente(null, "Carlos", "carlos@email.com",
                    AutenticacaoService.hash("carlos123"), "71966660000"));
        } else {
            barbearia.setAdministrador((Administrador) fabrica.usuarioDAO()
                    .buscarPorEmail("admin@barbertime.com").get());
        }

        if (fabrica.profissionalDAO().listar().isEmpty()) {
            Profissional joao = new Profissional(null, "João", "Corte masculino", "71988880000");
            DiaSemana[] diasJoao = {DiaSemana.SEGUNDA, DiaSemana.TERCA, DiaSemana.QUARTA,
                    DiaSemana.QUINTA, DiaSemana.SEXTA};
            for (DiaSemana dia : diasJoao) {
                joao.adicionarDisponibilidade(
                        new Disponibilidade(dia, LocalTime.of(9, 0), LocalTime.of(18, 0)));
            }
            joao.setBarbearia(barbearia);
            fabrica.profissionalDAO().inserir(joao);

            Profissional pedro = new Profissional(null, "Pedro", "Barba", "71955550000");
            DiaSemana[] diasPedro = {DiaSemana.TERCA, DiaSemana.QUARTA, DiaSemana.QUINTA,
                    DiaSemana.SEXTA, DiaSemana.SABADO};
            for (DiaSemana dia : diasPedro) {
                pedro.adicionarDisponibilidade(
                        new Disponibilidade(dia, LocalTime.of(10, 0), LocalTime.of(19, 0)));
            }
            pedro.setBarbearia(barbearia);
            fabrica.profissionalDAO().inserir(pedro);
        }

        if (fabrica.servicoDAO().listar().isEmpty()) {
            inserirServico(fabrica, barbearia, "Corte", "Corte tradicional", "35.00", 30);
            inserirServico(fabrica, barbearia, "Barba", "Barba na navalha", "25.00", 20);
            inserirServico(fabrica, barbearia, "Corte + Barba", "Combo", "55.00", 50);
        }
    }

    private static void inserirServico(FabricaDAO fabrica, Barbearia barbearia, String nome,
                                       String descricao, String preco, int minutos) {
        Servico s = new Servico(null, nome, descricao, new BigDecimal(preco), minutos);
        s.setBarbearia(barbearia);
        fabrica.servicoDAO().inserir(s);
    }
}
