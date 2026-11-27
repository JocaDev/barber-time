# BarberTime

#### Colaboradores:

##### Joabe Roseira dos Santos, Cailan Goes Santos e Daniel de Macedo Silva.
---

#### *Visando oferecer comodidade e facilidade de acesso o sistema tem como objetivo facilitar o contato para prestação de serviços em barbearias, entre cliente e administrador (responsável pelo estabelecimento).*

## API's

*A principio utilizaremos o ViaCEP para validar endereços.*

*O ViaCEP é uma API gratuita de consulta de CEPs brasileiros que permite obter informações de endereço, como logradouro, bairro, cidade, estado e código IBGE, a partir de um CEP informado pelo usuário.*

## Requisitos Funcionais

RF01: O sistema deve permitir o cadastro de clientes e administradores

RF02: O sistema deve permitir que os usuários realizem login e acessem as funcionalidades 
conforme seu perfil.

RF03: O sistema deve permitir o cadastro e gerenciamento de informações da barbearia.

RF04: O sistema deve permitir que o administrador cadastre e gerencie profissionais.

RF05: O sistema deve permitir o cadastro de serviços, descrições e preços.


## Requisitos Não Funcionais:

RNF01: Possuir interface simples e intuitiva.

RNF02: Permitir acesso ao sistema pela internet.

RNF03: Apresentar respostas rápidas às solicitações dos usuários.

RNF04: Ser acessível por computadores, tablets e smartphones.

RNF05: Garantir a integridade e consistência dos dados.

RNF06: Proteger os dados dos usuários e restringir acessos conforme seus perfis.

## Regras de Negócio:

RN01: Cada usuário deve possuir um cadastro único.

RN02: Os campos obrigatórios devem ser preenchidos durante o cadastro.

RN03: Somente horários disponíveis podem ser agendados.

RN04: Um mesmo horário não pode ser reservado por mais de um cliente para o mesmo profissional.

RN05: Todo agendamento realizado com sucesso deve gerar uma confirmação.

RN06: Cada profissional deve possuir sua própria disponibilidade de horários.

RN07: Não são permitidos horários de atendimento sobrepostos para o mesmo profissional.

RN08: Somente agendamentos existentes podem ser cancelados.

RN09: O cancelamento deve liberar o horário anteriormente reservado.

RN10: O novo horário escolhido deve estar disponível.

RN11: Ao reagendar, o horário anterior deve ser liberado.

RN12: Cada perfil deve acessar somente as funcionalidades permitidas.

RN13: Cada profissional deve estar associado a uma barbearia.

## API REST (Front Controller)

Subir o servidor: `mvn compile exec:java` (Tomcat embutido em `http://localhost:8080/api`).
Importar `BarberTime.postman_collection.json` no Postman e rodar a coleção em ordem.

Logins de exemplo: `admin@barbertime.com / admin123` (ADMINISTRADOR), `maria@email.com / maria123` e `carlos@email.com / carlos123` (CLIENTE).
Depois do login, enviar o token no header `Authorization: Bearer <token>`.

| Método | Caminho | CLIENTE | ADMINISTRADOR |
|---|---|---|---|
| POST | `/login` | público | público |
| GET | `/profissionais`, `/servicos` | sim | sim |
| POST | `/profissionais`, `/servicos` | 403 | sim |
| POST | `/agendamentos` | agenda para si | agenda para qualquer cliente (`clienteId` obrigatório) |
| GET | `/agendamentos` | só os seus | todos (filtros `clienteId`, `profissionalId`, `status`) |
| GET | `/agendamentos/{id}` | só o seu (senão 403) | qualquer um |
| PUT | `/agendamentos/{id}` (reagendar) | só o seu | qualquer um |
| DELETE | `/agendamentos/{id}` (cancelar) | só o seu | qualquer um |
| PATCH | `/agendamentos/{id}/concluir` | 403 | sim |
| DELETE | `/agendamentos/{id}/definitivo` | 403 | sim |

Status usados: 200/201/204 sucesso, 400 dados inválidos ou fora da agenda, 401 sem login, 403 sem permissão, 404 não encontrado, 409 conflito de horário ou estado inválido.

Padrões: Front Controller (`FrontControllerServlet`), Command (`command/`), Singleton (`ConnectionSingleton`), DTO e DAO.

## Teste automatizado do CRUD de agendamento (sem Tomcat)

`src/test/java/br/com/barbertime/TesteApiAgendamento.java` passa pelo mesmo `Roteador` do servlet.

## Padrões de projeto: onde cada um está

| Padrão | Onde |
|---|---|
| **Front Controller** | `servlet/FrontControllerServlet` (único servlet, mapeado em `/api/*`) e `command/Roteador` (acha a rota, valida token e perfil, traduz exceções em status HTTP) |
| **DTO** | pacote `dto/` (`*RequestDTO` na entrada, `*ResponseDTO` na saída); `command/Mapeador` converte entidade em DTO. A senha (`senhaHash`) nunca sai na API |
| **DAO** | interfaces em `dao/` (`UsuarioDAO`, `ProfissionalDAO`, `ServicoDAO`, `AgendamentoDAO`); implementações em `dao/memoria/` e `dao/postgres/`. Os services só conhecem as interfaces |
| **Command** (GoF) | `command/Command` e uma classe por operação (`CriarAgendamentoCommand`, `CancelarAgendamentoCommand`, ...), registradas na tabela de rotas do `Roteador` |
| **Abstract Factory** (GoF) | `dao/FabricaDAO` com `FabricaMemoria` e `FabricaPostgres`: cria a família de DAOs conforme a variável `PERSISTENCIA` |
| **Singleton** (GoF) | `infra/ConnectionSingleton` (uma conexão com o PostgreSQL) |

Camadas: Servlet/Roteador/Commands (controle) -> Services (regras de negócio) -> DAOs (persistência) -> Model (domínio).

## Persistência: memória ou PostgreSQL

Por padrão a aplicação usa **memória** (não precisa de banco; os dados somem ao reiniciar).
Para usar PostgreSQL, defina `PERSISTENCIA=postgres`.

1. Criar o banco e as tabelas, no `psql`:
   ```
   CREATE DATABASE barbertime_web;
   \c barbertime_web
   \cd C:/caminho/para/barber-time
   \i sql/schema.sql
   ```
2. Variáveis de ambiente (os valores abaixo são os padrões):
   `DB_URL=jdbc:postgresql://localhost:5432/barbertime_web`, `DB_USER=postgres`, `DB_PASSWORD=1234`.
3. Subir a API (PowerShell): `$env:PERSISTENCIA="postgres"; mvn compile exec:java`.
   Os dados de exemplo (usuários, profissionais, serviços) são criados na primeira subida e não são duplicados nas seguintes.

Observação: o banco guarda usuários, profissionais (com disponibilidades), serviços e agendamentos. A barbearia é uma só (id 1, inserida pelo `schema.sql`); o endereço dela (ViaCEP) e os tokens de login ficam apenas em memória.

## Testes

- API (CRUD de agendamento e perfis, sem Tomcat): `TesteApiAgendamento`.
- Contrato dos DAOs (o mesmo teste para memória e Postgres): `TesteContratoDAO`.
  ```
  mvn test-compile exec:java "-Dexec.mainClass=br.com.barbertime.TesteContratoDAO" "-Dexec.classpathScope=test"
  ```
  Com Postgres, defina antes `$env:PERSISTENCIA="postgres"`.
- Postman: importar `BarberTime.postman_collection.json` e rodar a coleção.
