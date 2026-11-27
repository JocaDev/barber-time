CREATE TABLE IF NOT EXISTS barbearia (
    id       BIGSERIAL PRIMARY KEY,
    nome     VARCHAR(120) NOT NULL,
    cnpj     VARCHAR(18)  NOT NULL UNIQUE,
    telefone VARCHAR(20)
);


INSERT INTO barbearia (id, nome, cnpj, telefone)
VALUES (1, 'Barbearia Exemplo', '00.000.000/0001-00', '7133330000')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('barbearia', 'id'), (SELECT MAX(id) FROM barbearia));

CREATE TABLE IF NOT EXISTS usuario (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(120) NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE,
    senha_hash VARCHAR(64)  NOT NULL,
    telefone   VARCHAR(20),
    perfil     VARCHAR(20)  NOT NULL CHECK (perfil IN ('CLIENTE', 'ADMINISTRADOR')),
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS profissional (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    especialidade VARCHAR(120),
    telefone      VARCHAR(20),
    ativo         BOOLEAN NOT NULL DEFAULT TRUE,
    barbearia_id  BIGINT  NOT NULL REFERENCES barbearia (id)      
);

CREATE TABLE IF NOT EXISTS disponibilidade (
    id              BIGSERIAL PRIMARY KEY,
    profissional_id BIGINT      NOT NULL REFERENCES profissional (id) ON DELETE CASCADE,
    dia_semana      VARCHAR(10) NOT NULL CHECK (dia_semana IN
                    ('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO')),
    hora_inicio     TIME NOT NULL,
    hora_fim        TIME NOT NULL,
    CHECK (hora_inicio < hora_fim)
);

CREATE TABLE IF NOT EXISTS servico (
    id              BIGSERIAL PRIMARY KEY,
    barbearia_id    BIGINT         NOT NULL REFERENCES barbearia (id),
    nome            VARCHAR(120)   NOT NULL,
    descricao       TEXT,
    preco           NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    duracao_minutos INT            NOT NULL CHECK (duracao_minutos > 0)
);

CREATE TABLE IF NOT EXISTS agendamento (
    id                 BIGSERIAL PRIMARY KEY,
    cliente_id         BIGINT      NOT NULL REFERENCES usuario (id),
    profissional_id    BIGINT      NOT NULL REFERENCES profissional (id),
    servico_id         BIGINT      NOT NULL REFERENCES servico (id),
    data_hora_inicio   TIMESTAMP   NOT NULL,
    data_hora_fim      TIMESTAMP   NOT NULL,
    status             VARCHAR(15) NOT NULL CHECK (status IN ('CONFIRMADO','CANCELADO','CONCLUIDO')),
    codigo_confirmacao VARCHAR(8)  NOT NULL UNIQUE,
    criado_em          TIMESTAMP   NOT NULL DEFAULT now(),
    CHECK (data_hora_fim > data_hora_inicio)
);

CREATE INDEX IF NOT EXISTS idx_agendamento_profissional_inicio
    ON agendamento (profissional_id, data_hora_inicio);
