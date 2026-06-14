CREATE TABLE usuario (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  email VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE evento (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(50) NOT NULL,
  descricao VARCHAR(500),
  data TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);

CREATE TABLE habito (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  frequencia_quantidade INTEGER NOT NULL,
  frequencia_unidade INTEGER NOT NULL,
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_habito_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);

CREATE TABLE objetivo (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(50) NOT NULL,
  descricao VARCHAR(500),
  status INTEGER NOT NULL,
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_objetivo_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);

CREATE TABLE tarefa (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(50) NOT NULL,
  descricao VARCHAR(500),
  prioridade INTEGER NOT NULL,
  concluida BOOLEAN NOT NULL DEFAULT FALSE,
  data_limite DATE,
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_tarefa_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);

INSERT INTO usuario (nome, email) 
VALUES ('Gustavo Rosa', 'gstvcaixeta@gmail.com');

INSERT INTO usuario (nome, email) 
VALUES ('Carol Peres', 'carolperes@gmail.com');
