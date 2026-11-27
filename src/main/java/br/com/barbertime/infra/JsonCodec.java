package br.com.barbertime.infra;


public interface JsonCodec {

    
    <T> T ler(String json, Class<T> tipo);

    String escrever(Object objeto);
}
