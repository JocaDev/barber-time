package br.com.barbertime.command;

import br.com.barbertime.infra.DadosIniciais;
import br.com.barbertime.infra.JsonCodec;
import br.com.barbertime.service.AgendamentoService;
import br.com.barbertime.service.AutenticacaoService;
import br.com.barbertime.service.CatalogoService;


public class ContextoAplicacao {

    public final AutenticacaoService autenticacao;
    public final CatalogoService catalogo;
    public final AgendamentoService agendamentos;
    public final JsonCodec codec;

    public ContextoAplicacao(AutenticacaoService autenticacao, CatalogoService catalogo,
                             AgendamentoService agendamentos, JsonCodec codec) {
        this.autenticacao = autenticacao;
        this.catalogo = catalogo;
        this.agendamentos = agendamentos;
        this.codec = codec;
    }

    
    public static ContextoAplicacao comDadosIniciais(JsonCodec codec) {
        return DadosIniciais.criar(codec);
    }
}
