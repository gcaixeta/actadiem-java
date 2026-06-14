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

INSERT INTO usuario (nome, email) 
VALUES ('Gustavo Rosa', 'gstvcaixeta@gmail.com');




