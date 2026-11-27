package br.com.barbertime.command;

import br.com.barbertime.exception.AcessoNegadoException;
import br.com.barbertime.exception.ConflitoException;
import br.com.barbertime.exception.NaoAutenticadoException;
import br.com.barbertime.exception.RecursoNaoEncontradoException;
import br.com.barbertime.model.Perfil;
import br.com.barbertime.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Roteador {

    private static final List<Perfil> QUALQUER_LOGADO = List.of();
    private static final List<Perfil> SO_ADMIN = List.of(Perfil.ADMINISTRADOR);

    private final ContextoAplicacao ctx;
    private final List<Rota> rotas = new ArrayList<>();

    public Roteador(ContextoAplicacao ctx) {
        this.ctx = ctx;

        // público
        rotas.add(new Rota("POST", "/login", true, QUALQUER_LOGADO, new LoginCommand(ctx)));

        // cliente e administrador
        rotas.add(new Rota("GET", "/profissionais", false, QUALQUER_LOGADO,
                new ListarProfissionaisCommand(ctx)));
        rotas.add(new Rota("GET", "/servicos", false, QUALQUER_LOGADO,
                new ListarServicosCommand(ctx)));
        rotas.add(new Rota("POST", "/agendamentos", false, QUALQUER_LOGADO,
                new CriarAgendamentoCommand(ctx)));
        rotas.add(new Rota("GET", "/agendamentos", false, QUALQUER_LOGADO,
                new ListarAgendamentosCommand(ctx)));
        rotas.add(new Rota("GET", "/agendamentos/{id}", false, QUALQUER_LOGADO,
                new BuscarAgendamentoCommand(ctx)));
        rotas.add(new Rota("PUT", "/agendamentos/{id}", false, QUALQUER_LOGADO,
                new ReagendarAgendamentoCommand(ctx)));
        rotas.add(new Rota("DELETE", "/agendamentos/{id}", false, QUALQUER_LOGADO,
                new CancelarAgendamentoCommand(ctx)));

        // somente administrador
        rotas.add(new Rota("POST", "/profissionais", false, SO_ADMIN,
                new CriarProfissionalCommand(ctx)));
        rotas.add(new Rota("POST", "/servicos", false, SO_ADMIN,
                new CriarServicoCommand(ctx)));
        rotas.add(new Rota("PATCH", "/agendamentos/{id}/concluir", false, SO_ADMIN,
                new ConcluirAgendamentoCommand(ctx)));
        rotas.add(new Rota("DELETE", "/agendamentos/{id}/definitivo", false, SO_ADMIN,
                new RemoverAgendamentoCommand(ctx)));
    }

    public Resposta processar(String metodo, String caminho, Map<String, String> query,
                              String corpo, String cabecalhoAuthorization) {
        try {
            Rota rota = null;
            Map<String, String> parametros = null;
            boolean caminhoExiste = false;

            for (Rota r : rotas) {
                Map<String, String> p = r.casar(caminho);
                if (p != null) {
                    caminhoExiste = true;
                    if (r.getMetodo().equalsIgnoreCase(metodo)) {
                        rota = r;
                        parametros = p;
                        break;
                    }
                }
            }
            if (rota == null) {
                return caminhoExiste
                        ? Resposta.erro(405, "Método " + metodo + " não permitido para este caminho.")
                        : Resposta.erro(404, "Rota não encontrada: " + caminho);
            }

            Requisicao req = new Requisicao(metodo, parametros, query, corpo, ctx.codec);

            if (!rota.isPublica()) {
                Usuario usuario = ctx.autenticacao.usuarioDoToken(extrairToken(cabecalhoAuthorization));
                if (!rota.getPerfisPermitidos().isEmpty()
                        && !rota.getPerfisPermitidos().contains(usuario.getPerfil())) {
                    throw new AcessoNegadoException(
                            "Operação restrita ao perfil " + rota.getPerfisPermitidos().get(0) + ".");
                }
                req.setUsuario(usuario);
            }
            return rota.getCommand().executar(req);

        } catch (NaoAutenticadoException e) {
            return Resposta.erro(401, e.getMessage());
        } catch (AcessoNegadoException e) {
            return Resposta.erro(403, e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            return Resposta.erro(404, e.getMessage());
        } catch (ConflitoException | IllegalStateException e) {
            return Resposta.erro(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return Resposta.erro(400, e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Resposta.erro(500, "Erro interno do servidor.");
        }
    }

    private String extrairToken(String cabecalho) {
        if (cabecalho == null) {
            return null;
        }
        String valor = cabecalho.trim();
        if (valor.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return valor.substring(7).trim();
        }
        return valor;
    }
}
