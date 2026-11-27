package br.com.barbertime.command;


public interface Command {

    Resposta executar(Requisicao requisicao);
}
