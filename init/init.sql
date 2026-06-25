CREATE TABLE usuario (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  email VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE evento (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(50) NOT NULL,
  descricao VARCHAR(500),
  nota INTEGER CHECK (nota IS NULL OR nota BETWEEN 1 AND 5),
  comentario VARCHAR(500),
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

CREATE TABLE material (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(50) NOT NULL,
  descricao VARCHAR(500),
  tipo INTEGER NOT NULL,
  url VARCHAR(500),
  usuario_id BIGINT NOT NULL,
  CONSTRAINT fk_material_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario(id)
    ON DELETE CASCADE
);

CREATE TABLE material_tarefa (
  material_id BIGINT NOT NULL,
  tarefa_id BIGINT NOT NULL,
  PRIMARY KEY (material_id, tarefa_id),
  CONSTRAINT fk_mt_material
    FOREIGN KEY (material_id)
    REFERENCES material(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_mt_tarefa
    FOREIGN KEY (tarefa_id)
    REFERENCES tarefa(id)
    ON DELETE CASCADE
);

CREATE TABLE material_objetivo (
  material_id BIGINT NOT NULL,
  objetivo_id BIGINT NOT NULL,
  PRIMARY KEY (material_id, objetivo_id),
  CONSTRAINT fk_mo_material
    FOREIGN KEY (material_id)
    REFERENCES material(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_mo_objetivo
    FOREIGN KEY (objetivo_id)
    REFERENCES objetivo(id)
    ON DELETE CASCADE
);

CREATE TABLE evento_objetivo (
  evento_id BIGINT NOT NULL,
  objetivo_id BIGINT NOT NULL,
  PRIMARY KEY (evento_id, objetivo_id),
  CONSTRAINT fk_eo_evento
    FOREIGN KEY (evento_id)
    REFERENCES evento(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_eo_objetivo
    FOREIGN KEY (objetivo_id)
    REFERENCES objetivo(id)
    ON DELETE CASCADE
);

INSERT INTO usuario (nome, email)
VALUES ('Gustavo Rosa', 'gstvcaixeta@gmail.com');

INSERT INTO usuario (nome, email) 
VALUES ('Carol Peres', 'carolperes@gmail.com');
