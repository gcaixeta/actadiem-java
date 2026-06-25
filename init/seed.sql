-- Dados de demonstração para a Revisão Diária.
--
-- A Revisão Diária analisa os eventos do DIA ANTERIOR, então os eventos abaixo
-- são datados como (CURRENT_DATE - 1). As datas são relativas ao momento em que
-- este script roda: execute-o no mesmo dia em que for testar o app.
--
-- Roda automaticamente em uma inicialização limpa do banco (Docker executa os
-- arquivos de /docker-entrypoint-initdb.d em ordem alfabética, depois de init.sql).
-- Para aplicar em um banco já existente, garanta antes que a tabela evento tenha
-- as colunas nota/comentario (ALTER TABLE), e então rode este arquivo.
--
-- Tudo é criado para o usuário de id 1 (o usuário ativo padrão).

-- Eventos fora da janela da revisão (hoje e anteontem) — não devem aparecer.
INSERT INTO evento (titulo, descricao, data, usuario_id) VALUES
  ('Planejamento da semana', 'Organizar prioridades', CURRENT_DATE, 1),
  ('Café com a equipe', 'Conversa informal', CURRENT_DATE - 2, 1);

-- Objetivos de contexto + eventos de ontem (alvos da revisão), já vinculados.
-- "Deep work" já vem avaliado para demonstrar o pré-preenchimento na re-revisão.
WITH obj AS (
  INSERT INTO objetivo (titulo, descricao, status, usuario_id) VALUES
    ('Melhorar foco', 'Reduzir distrações durante o trabalho', 3, 1),
    ('Cuidar da saúde', 'Exercícios e sono em dia', 2, 1)
  RETURNING id, titulo
),
ev AS (
  INSERT INTO evento (titulo, descricao, nota, comentario, data, usuario_id) VALUES
    ('Reunião com cliente', 'Alinhamento do escopo do projeto', NULL, NULL, CURRENT_DATE - 1, 1),
    ('Treino na academia', 'Treino de pernas', NULL, NULL, CURRENT_DATE - 1, 1),
    ('Leitura técnica', 'Capítulo sobre concorrência em Java', NULL, NULL, CURRENT_DATE - 1, 1),
    ('Deep work', 'Bloco de 2h sem interrupções', 4, 'Rendeu bastante, repetir amanhã', CURRENT_DATE - 1, 1)
  RETURNING id, titulo
)
INSERT INTO evento_objetivo (evento_id, objetivo_id)
SELECT ev.id, obj.id
FROM ev
JOIN obj ON (
     (ev.titulo = 'Reunião com cliente' AND obj.titulo = 'Melhorar foco')
  OR (ev.titulo = 'Deep work'           AND obj.titulo = 'Melhorar foco')
  OR (ev.titulo = 'Treino na academia'  AND obj.titulo = 'Cuidar da saúde')
);
