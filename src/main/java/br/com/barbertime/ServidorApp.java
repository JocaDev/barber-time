package br.com.barbertime;

import br.com.barbertime.command.ContextoAplicacao;
import br.com.barbertime.command.Roteador;
import br.com.barbertime.infra.GsonCodec;
import br.com.barbertime.infra.JsonCodec;
import br.com.barbertime.servlet.FrontControllerServlet;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.nio.file.Files;
import java.nio.file.Path;

/** Sobe o Tomcat embutido em http://localhost:8080 (ou na porta da variável PORT). */
public class ServidorApp {

    public static void main(String[] args) throws Exception {
        int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        JsonCodec codec = new GsonCodec();
        ContextoAplicacao ctx = ContextoAplicacao.comDadosIniciais(codec);
        Roteador roteador = new Roteador(ctx);

        Path base = Files.createTempDirectory("barbertime-tomcat");

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(base.toString());
        tomcat.setPort(porta);
        tomcat.getConnector();

        Context contexto = tomcat.addContext("", base.toString());
        Tomcat.addServlet(contexto, "frontController", new FrontControllerServlet(roteador, codec));
        contexto.addServletMappingDecoded("/api/*", "frontController");

        tomcat.start();
        System.out.println("Persistência: "
                + System.getenv().getOrDefault("PERSISTENCIA", "memoria"));
        System.out.println("BarberTime no ar: http://localhost:" + porta + "/api");
        System.out.println("Logins: admin@barbertime.com/admin123 | maria@email.com/maria123"
                + " | carlos@email.com/carlos123");
        tomcat.getServer().await();
    }
}
