-- Dados de demonstração para a apresentação do app.
--
-- Garante pelo menos 3 registros em CADA tabela, cobrindo todas as features:
-- usuários, objetivos, tarefas, hábitos, materiais, eventos e seus
-- relacionamentos (material<->tarefa, material<->objetivo, evento<->objetivo).
--
-- A Revisão Diária analisa os eventos do DIA ANTERIOR, então parte dos eventos
-- abaixo é datada como (CURRENT_DATE - 1). As datas são RELATIVAS ao momento em
-- que este script roda: execute-o no mesmo dia em que for gravar o vídeo.
--
-- Roda automaticamente em uma inicialização limpa do banco (Docker executa os
-- arquivos de /docker-entrypoint-initdb.d em ordem alfabética, depois de init.sql).
-- A maior parte dos dados é do usuário de id 1 (o usuário ativo padrão).

-- ---------------------------------------------------------------------------
-- USUÁRIO  (init.sql já cria 2; este é o terceiro)
-- ---------------------------------------------------------------------------
INSERT INTO usuario (nome, email) VALUES
  ('João Mendes', 'joaomendes@gmail.com');

-- ---------------------------------------------------------------------------
-- OBJETIVO
-- status: 1=EM_PLANEJAMENTO 2=PLANEJADO 3=EM_EXECUCAO 4=ALCANCADO 5=NAO_ALCANCADO
-- ---------------------------------------------------------------------------
INSERT INTO objetivo (titulo, descricao, status, usuario_id) VALUES
  ('Melhorar foco',         'Reduzir distrações durante o trabalho',        3, 1),
  ('Cuidar da saúde',       'Manter exercícios e sono em dia',              2, 1),
  ('Aprender Java avançado','Dominar concorrência, streams e JDBC',         3, 1),
  ('Ler 12 livros no ano',  'Um livro por mês até dezembro',                1, 1);

-- ---------------------------------------------------------------------------
-- TAREFA
-- prioridade: 1=BAIXA 2=MEDIA 3=GRANDE
-- ---------------------------------------------------------------------------
INSERT INTO tarefa (titulo, descricao, prioridade, concluida, data_limite, usuario_id) VALUES
  ('Configurar ambiente Docker', 'Subir Postgres com docker-compose',        3, TRUE,  CURRENT_DATE - 3, 1),
  ('Estudar concorrência',       'Threads, locks e executors em Java',       2, FALSE, CURRENT_DATE + 5, 1),
  ('Agendar consulta médica',    'Check-up anual de rotina',                 1, FALSE, CURRENT_DATE + 10, 1),
  ('Revisar PR do colega',       'Code review do módulo de relatórios',      2, TRUE,  CURRENT_DATE - 1, 1);

-- ---------------------------------------------------------------------------
-- HÁBITO
-- frequencia_unidade: 1=DIA 2=SEMANA 3=MES 4=ANO
-- ---------------------------------------------------------------------------
INSERT INTO habito (nome, frequencia_quantidade, frequencia_unidade, usuario_id) VALUES
  ('Beber 2L de água',     1, 1, 1),
  ('Correr',               3, 2, 1),
  ('Ler antes de dormir',  1, 1, 1),
  ('Meditar',              5, 2, 1);

-- ---------------------------------------------------------------------------
-- MATERIAL
-- tipo: 1=VIDEO 2=LIVRO 3=EBOOK 4=ARTIGO 5=PODCAST 6=DOCUMENTACAO 7=OUTRO
-- ---------------------------------------------------------------------------
INSERT INTO material (titulo, descricao, tipo, url, usuario_id) VALUES
  ('Effective Java',               'Boas práticas da linguagem Java', 2, NULL,                                          1),
  ('Java Concurrency in Practice', 'Referência sobre concorrência',   2, NULL,                                          1),
  ('Documentação do PostgreSQL',   'Manual oficial do Postgres',      6, 'https://www.postgresql.org/docs/',            1),
  ('Clean Code (resumo em vídeo)', 'Princípios de código limpo',      1, 'https://www.youtube.com/watch?v=7EmboKQH8lM', 1);

-- ---------------------------------------------------------------------------
-- EVENTO
-- Eventos fora da janela da revisão (hoje e anteontem) — não devem aparecer
-- na Revisão Diária, mas demonstram o histórico de registros.
-- "Deep work" já vem avaliado (nota/comentário) para demonstrar o
-- pré-preenchimento na re-revisão.
-- ---------------------------------------------------------------------------
INSERT INTO evento (titulo, descricao, nota, comentario, data, usuario_id) VALUES
  ('Planejamento da semana', 'Organizar prioridades',                 NULL, NULL,                               CURRENT_DATE,     1),
  ('Café com a equipe',      'Conversa informal',                     NULL, NULL,                               CURRENT_DATE - 2, 1),
  ('Reunião com cliente',    'Alinhamento do escopo do projeto',      NULL, NULL,                               CURRENT_DATE - 1, 1),
  ('Treino na academia',     'Treino de pernas',                      NULL, NULL,                               CURRENT_DATE - 1, 1),
  ('Leitura técnica',        'Capítulo sobre concorrência em Java',   NULL, NULL,                               CURRENT_DATE - 1, 1),
  ('Deep work',              'Bloco de 2h sem interrupções',          4,    'Rendeu bastante, repetir amanhã',  CURRENT_DATE - 1, 1);

-- ---------------------------------------------------------------------------
-- RELACIONAMENTOS
-- Ligados por título (únicos dentro deste seed, todos do usuário 1).
-- ---------------------------------------------------------------------------

-- evento <-> objetivo
INSERT INTO evento_objetivo (evento_id, objetivo_id)
SELECT e.id, o.id
FROM evento e
JOIN objetivo o ON o.usuario_id = 1 AND e.usuario_id = 1 AND (
     (e.titulo = 'Reunião com cliente' AND o.titulo = 'Melhorar foco')
  OR (e.titulo = 'Deep work'           AND o.titulo = 'Melhorar foco')
  OR (e.titulo = 'Treino na academia'  AND o.titulo = 'Cuidar da saúde')
  OR (e.titulo = 'Leitura técnica'     AND o.titulo = 'Aprender Java avançado')
);

-- material <-> tarefa
INSERT INTO material_tarefa (material_id, tarefa_id)
SELECT m.id, t.id
FROM material m
JOIN tarefa t ON m.usuario_id = 1 AND t.usuario_id = 1 AND (
     (m.titulo = 'Java Concurrency in Practice' AND t.titulo = 'Estudar concorrência')
  OR (m.titulo = 'Documentação do PostgreSQL'   AND t.titulo = 'Configurar ambiente Docker')
  OR (m.titulo = 'Clean Code (resumo em vídeo)' AND t.titulo = 'Revisar PR do colega')
);

-- material <-> objetivo
INSERT INTO material_objetivo (material_id, objetivo_id)
SELECT m.id, o.id
FROM material m
JOIN objetivo o ON m.usuario_id = 1 AND o.usuario_id = 1 AND (
     (m.titulo = 'Effective Java'               AND o.titulo = 'Aprender Java avançado')
  OR (m.titulo = 'Java Concurrency in Practice' AND o.titulo = 'Aprender Java avançado')
  OR (m.titulo = 'Documentação do PostgreSQL'   AND o.titulo = 'Melhorar foco')
);
