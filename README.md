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

## Diagrama de Classes:

<img width="3136" height="4548" alt="Diagrama de Classes" src="https://github.com/user-attachments/assets/fc3aadf8-bba8-44f4-9717-0a6d16f56da3" />

## Diagrama de Comportamentos

<img width="3136" height="1956" alt="Diagrama de Comportamentos" src="https://github.com/user-attachments/assets/e42765fb-f67e-4f9a-a786-fdcdb3f0a6fb" />

## Modelo de Banco de Dados 

<img width="3136" height="4304" alt="Modelo de Banco de Dados" src="https://github.com/user-attachments/assets/ba48dcf2-cc64-427e-b85a-3d91956fc623" />


