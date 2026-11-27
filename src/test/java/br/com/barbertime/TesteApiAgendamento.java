package br.com.barbertime;

import br.com.barbertime.command.ContextoAplicacao;
import br.com.barbertime.command.Resposta;
import br.com.barbertime.command.Roteador;
import br.com.barbertime.dto.AgendamentoResponseDTO;
import br.com.barbertime.dto.LoginResponseDTO;
import br.com.barbertime.model.StatusAgendamento;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testa o CRUD de agendamento e a separação CLIENTE x ADMINISTRADOR passando pelo mesmo
 * Roteador que o Servlet usa (só não passa pelo Tomcat/Gson). Rode com: java TesteApiAgendamento
 */
public class TesteApiAgendamento {

    private static int ok = 0;
    private static int falhas = 0;

    private static Roteador api;
    private static String admin;
    private static String maria;
    private static String carlos;

    public static void main(String[] args) {
        api = new Roteador(ContextoAplicacao.comDadosIniciais(new CodecDeTeste()));

        testarLogin();
        testarAutenticacaoObrigatoria();
        testarCriarAgendamento();
        testarListarELer();
        testarReagendar();
        testarCancelar();
        testarPerfilAdministrador();
        testarRotasEErros();

        System.out.println("\n" + ok + " verificações OK, " + falhas + " falhas.");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    // ---------------------------------------------------------------- testes

    private static void testarLogin() {
        titulo("LOGIN");
        admin = logar("admin@barbertime.com", "admin123");
        maria = logar("maria@email.com", "maria123");
        carlos = logar("carlos@email.com", "carlos123");
        verificar(admin != null && maria != null && carlos != null, "3 logins válidos devolvem token");

        Resposta r = chamar("POST", "/login", null, "{\"email\":\"maria@email.com\",\"senha\":\"errada\"}", null);
        esperar(401, r, "senha errada -> 401");
        r = chamar("POST", "/login", null, "", null);
        esperar(400, r, "login sem corpo -> 400");
    }

    private static void testarAutenticacaoObrigatoria() {
        titulo("TOKEN OBRIGATÓRIO");
        esperar(401, chamar("GET", "/agendamentos", null, null, null), "sem token -> 401");
        esperar(401, chamar("GET", "/agendamentos", null, null, "lixo"), "token inválido -> 401");
    }

    private static void testarCriarAgendamento() {
        titulo("CREATE (POST /agendamentos)");
        LocalDateTime seg10 = proximo(DayOfWeek.MONDAY, 10);

        Resposta r = agendar(maria, 1, 1, seg10, null);
        esperar(201, r, "cliente agenda dentro da agenda do João -> 201");
        AgendamentoResponseDTO a = dto(r);
        verificar(a.getStatus() == StatusAgendamento.CONFIRMADO, "status CONFIRMADO");
        verificar(a.getCodigoConfirmacao() != null && a.getCodigoConfirmacao().length() == 8,
                "gera código de confirmação (RN05)");
        verificar(a.getClienteId() == 2L, "agendamento pertence à Maria");
        verificar(a.getDataHoraFim().equals(seg10.plusMinutes(30)), "fim = início + duração do serviço");

        esperar(409, agendar(carlos, 1, 1, seg10, null), "mesmo profissional/horário -> 409 (RN04)");
        esperar(409, agendar(carlos, 1, 1, seg10.plusMinutes(15), null), "sobreposição parcial -> 409 (RN04)");
        esperar(201, agendar(carlos, 1, 1, seg10.plusMinutes(30), null), "horário colado, sem sobrepor -> 201");
        esperar(201, agendar(carlos, 2, 1, proximo(DayOfWeek.TUESDAY, 10), null),
                "outro profissional no mesmo horário -> 201");

        esperar(400, agendar(maria, 1, 1, proximo(DayOfWeek.MONDAY, 20), null), "fora do expediente -> 400 (RN03)");
        esperar(400, agendar(maria, 1, 1, proximo(DayOfWeek.SUNDAY, 10), null), "dia sem atendimento -> 400 (RN03)");
        esperar(400, agendar(maria, 1, 1, LocalDateTime.now().minusDays(1), null), "no passado -> 400");
        esperar(404, agendar(maria, 99, 1, seg10, null), "profissional inexistente -> 404");
        esperar(404, agendar(maria, 1, 99, seg10, null), "serviço inexistente -> 404");
        esperar(400, chamar("POST", "/agendamentos", null,
                "{\"profissionalId\":1,\"servicoId\":1}", maria), "sem dataHoraInicio -> 400 (RN02)");
        esperar(400, chamar("POST", "/agendamentos", null,
                "{\"profissionalId\":1,\"servicoId\":1,\"dataHoraInicio\":\"amanha\"}", maria),
                "data em formato inválido -> 400");
        esperar(400, chamar("POST", "/agendamentos", null, "", maria), "corpo vazio -> 400");
    }

    private static void testarListarELer() {
        titulo("READ (GET /agendamentos e /agendamentos/{id})");
        List<AgendamentoResponseDTO> daMaria = lista(chamar("GET", "/agendamentos", null, null, maria));
        verificar(daMaria.size() == 1 && daMaria.get(0).getClienteId() == 2L,
                "Maria vê só o próprio agendamento");
        List<AgendamentoResponseDTO> doCarlos = lista(chamar("GET", "/agendamentos", null, null, carlos));
        verificar(doCarlos.size() == 2, "Carlos vê só os 2 dele");
        List<AgendamentoResponseDTO> todos = lista(chamar("GET", "/agendamentos", null, null, admin));
        verificar(todos.size() == 3, "administrador vê todos (3)");

        Map<String, String> q = new HashMap<>();
        q.put("clienteId", "3");
        verificar(lista(chamar("GET", "/agendamentos", q, null, admin)).size() == 2,
                "admin filtra ?clienteId=3");
        q = new HashMap<>();
        q.put("clienteId", "3");
        verificar(lista(chamar("GET", "/agendamentos", q, null, maria)).size() == 1,
                "cliente não consegue burlar com ?clienteId (continua vendo só os seus)");
        q = new HashMap<>();
        q.put("status", "CANCELADO");
        verificar(lista(chamar("GET", "/agendamentos", q, null, admin)).isEmpty(), "filtro ?status=CANCELADO vazio");
        q = new HashMap<>();
        q.put("status", "XPTO");
        esperar(400, chamar("GET", "/agendamentos", q, null, admin), "status inválido -> 400");

        long idMaria = daMaria.get(0).getId();
        esperar(200, chamar("GET", "/agendamentos/" + idMaria, null, null, maria), "dono lê o seu -> 200");
        esperar(403, chamar("GET", "/agendamentos/" + idMaria, null, null, carlos), "outro cliente lê -> 403 (RN12)");
        esperar(200, chamar("GET", "/agendamentos/" + idMaria, null, null, admin), "admin lê qualquer -> 200");
        esperar(404, chamar("GET", "/agendamentos/999", null, null, admin), "id inexistente -> 404");
        esperar(400, chamar("GET", "/agendamentos/abc", null, null, admin), "id não numérico -> 400");
    }

    private static void testarReagendar() {
        titulo("UPDATE (PUT /agendamentos/{id} = reagendar)");
        long idMaria = lista(chamar("GET", "/agendamentos", null, null, maria)).get(0).getId();
        LocalDateTime novo = proximo(DayOfWeek.WEDNESDAY, 14);

        Resposta r = chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(novo), maria);
        esperar(200, r, "dono reagenda -> 200");
        verificar(dto(r).getDataHoraInicio().equals(novo), "novo horário gravado");

        // liberou segunda 10h (RN11): Carlos consegue pegar
        esperar(201, agendar(carlos, 1, 1, proximo(DayOfWeek.MONDAY, 10), null),
                "horário antigo ficou livre (RN11)");

        esperar(409, chamar("PUT", "/agendamentos/" + idMaria, null,
                corpoNovaData(proximo(DayOfWeek.MONDAY, 10)), maria),
                "reagendar para horário ocupado -> 409 (RN10)");
        esperar(403, chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(novo.plusHours(1)), carlos),
                "outro cliente reagenda -> 403");
        esperar(200, chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(novo.plusHours(1)), admin),
                "admin reagenda qualquer -> 200");
        esperar(200, chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(novo.plusHours(1)), maria),
                "reagendar para o próprio horário atual -> 200 (ignora a si mesmo)");
        esperar(400, chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(proximo(DayOfWeek.SUNDAY, 10)),
                maria), "reagendar fora da agenda -> 400");
        esperar(400, chamar("PUT", "/agendamentos/" + idMaria, null, "{}", maria), "sem novaDataHora -> 400");
    }

    private static void testarCancelar() {
        titulo("DELETE (DELETE /agendamentos/{id} = cancelar)");
        long idMaria = lista(chamar("GET", "/agendamentos", null, null, maria)).get(0).getId();
        LocalDateTime horarioDela = dto(chamar("GET", "/agendamentos/" + idMaria, null, null, maria))
                .getDataHoraInicio();

        esperar(403, chamar("DELETE", "/agendamentos/" + idMaria, null, null, carlos), "outro cliente cancela -> 403");
        Resposta r = chamar("DELETE", "/agendamentos/" + idMaria, null, null, maria);
        esperar(200, r, "dono cancela -> 200");
        verificar(dto(r).getStatus() == StatusAgendamento.CANCELADO, "status CANCELADO");
        esperar(409, chamar("DELETE", "/agendamentos/" + idMaria, null, null, maria), "cancelar de novo -> 409");
        esperar(409, chamar("PUT", "/agendamentos/" + idMaria, null, corpoNovaData(horarioDela), maria),
                "reagendar cancelado -> 409");
        esperar(201, agendar(carlos, 1, 1, horarioDela, null), "cancelamento liberou o horário (RN09)");
        esperar(404, chamar("DELETE", "/agendamentos/999", null, null, maria), "cancelar inexistente -> 404 (RN08)");

        Map<String, String> q = new HashMap<>();
        q.put("status", "CANCELADO");
        verificar(lista(chamar("GET", "/agendamentos", q, null, maria)).size() == 1,
                "cancelado continua no histórico da Maria");
    }

    private static void testarPerfilAdministrador() {
        titulo("PERFIS: ADMINISTRADOR x CLIENTE");
        LocalDateTime quinta = proximo(DayOfWeek.THURSDAY, 15);

        // admin agenda em nome de cliente
        Resposta r = agendar(admin, 1, 2, quinta, 3L);
        esperar(201, r, "admin agenda em nome do Carlos -> 201");
        AgendamentoResponseDTO a = dto(r);
        verificar(a.getClienteId() == 3L, "agendamento ficou no nome do Carlos");
        esperar(400, agendar(admin, 1, 2, quinta.plusHours(2), null), "admin sem clienteId -> 400");
        esperar(400, agendar(admin, 1, 2, quinta.plusHours(2), 1L), "clienteId de administrador -> 400");
        esperar(404, agendar(admin, 1, 2, quinta.plusHours(2), 99L), "clienteId inexistente -> 404");

        // cliente tenta se passar por outro: clienteId é ignorado
        Resposta golpe = agendar(maria, 1, 2, quinta.plusHours(1), 3L);
        esperar(201, golpe, "cliente enviando clienteId alheio -> 201");
        verificar(dto(golpe).getClienteId() == 2L, "...mas o agendamento fica no nome dele mesmo (Maria)");

        // concluir
        esperar(403, chamar("PATCH", "/agendamentos/" + a.getId() + "/concluir", null, null, carlos),
                "cliente concluir -> 403");
        Resposta c = chamar("PATCH", "/agendamentos/" + a.getId() + "/concluir", null, null, admin);
        esperar(200, c, "admin conclui -> 200");
        verificar(dto(c).getStatus() == StatusAgendamento.CONCLUIDO, "status CONCLUIDO");
        esperar(409, chamar("DELETE", "/agendamentos/" + a.getId(), null, null, admin),
                "cancelar concluído -> 409");
        esperar(409, chamar("PATCH", "/agendamentos/" + a.getId() + "/concluir", null, null, admin),
                "concluir de novo -> 409");

        // exclusão definitiva
        esperar(403, chamar("DELETE", "/agendamentos/" + a.getId() + "/definitivo", null, null, carlos),
                "cliente excluir definitivo -> 403");
        esperar(204, chamar("DELETE", "/agendamentos/" + a.getId() + "/definitivo", null, null, admin),
                "admin exclui definitivo -> 204");
        esperar(404, chamar("GET", "/agendamentos/" + a.getId(), null, null, admin), "...e some do sistema -> 404");

        // catálogo: leitura para todos, escrita só admin
        esperar(200, chamar("GET", "/profissionais", null, null, maria), "cliente lista profissionais -> 200");
        esperar(200, chamar("GET", "/servicos", null, null, maria), "cliente lista serviços -> 200");
        String servico = "{\"nome\":\"Sobrancelha\",\"descricao\":\"na pinça\",\"preco\":15,\"duracaoMinutos\":15}";
        esperar(403, chamar("POST", "/servicos", null, servico, maria), "cliente cadastra serviço -> 403");
        esperar(403, chamar("POST", "/profissionais", null, "{\"nome\":\"Lucas\"}", maria),
                "cliente cadastra profissional -> 403");
        esperar(201, chamar("POST", "/profissionais", null, "{\"nome\":\"Lucas\"}", admin),
                "admin cadastra profissional -> 201");
    }

    private static void testarRotasEErros() {
        titulo("ROTAS");
        esperar(404, chamar("GET", "/nada", null, null, admin), "rota inexistente -> 404");
        esperar(405, chamar("PUT", "/agendamentos", null, "{}", admin), "método errado -> 405");
        esperar(200, chamar("GET", "/agendamentos/", null, null, admin), "barra final é aceita");
    }

    // ---------------------------------------------------------------- helpers

    private static String logar(String email, String senha) {
        Resposta r = chamar("POST", "/login", null,
                "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}", null);
        return r.getStatus() == 200 ? ((LoginResponseDTO) r.getCorpo()).getToken() : null;
    }

    private static Resposta agendar(String token, long profissionalId, long servicoId,
                                    LocalDateTime inicio, Long clienteId) {
        String corpo = "{\"profissionalId\":" + profissionalId + ",\"servicoId\":" + servicoId
                + ",\"dataHoraInicio\":\"" + inicio + "\""
                + (clienteId != null ? ",\"clienteId\":" + clienteId : "") + "}";
        return chamar("POST", "/agendamentos", null, corpo, token);
    }

    private static String corpoNovaData(LocalDateTime d) {
        return "{\"novaDataHora\":\"" + d + "\"}";
    }

    private static Resposta chamar(String metodo, String caminho, Map<String, String> query,
                                   String corpo, String token) {
        return api.processar(metodo, caminho, query == null ? new HashMap<>() : query, corpo,
                token == null ? null : "Bearer " + token);
    }

    private static AgendamentoResponseDTO dto(Resposta r) {
        return (AgendamentoResponseDTO) r.getCorpo();
    }

    @SuppressWarnings("unchecked")
    private static List<AgendamentoResponseDTO> lista(Resposta r) {
        return (List<AgendamentoResponseDTO>) r.getCorpo();
    }

    /** Próxima ocorrência do dia da semana, na hora cheia informada (sempre no futuro). */
    private static LocalDateTime proximo(DayOfWeek dia, int hora) {
        return LocalDateTime.now().with(TemporalAdjusters.next(dia))
                .withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }

    private static void titulo(String t) {
        System.out.println("\n== " + t);
    }

    private static void esperar(int statusEsperado, Resposta r, String descricao) {
        verificar(r.getStatus() == statusEsperado,
                descricao + (r.getStatus() == statusEsperado ? "" : "  [veio " + r.getStatus()
                        + ": " + r.getCorpo() + "]"));
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
