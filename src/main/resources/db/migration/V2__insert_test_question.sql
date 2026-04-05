insert into questions (
    id, section, topic, type, text, explanation, translation, difficulty, source_type, active
) values (
    1,
    'Prompting Basics',
    'Description',
    'SINGLE_CHOICE',
    'Which competency focuses on communicating clearly with AI systems about what you want?',
    'Description is about clearly defining what you want the AI to produce.',
    null,
    'EASY',
    'STATIC',
    true
);

insert into answer_options (id, text, correct, question_id) values
    (1, 'Discernment', false, 1),
    (2, 'Description', true, 1),
    (3, 'Delegation', false, 1),
    (4, 'Diligence', false, 1);

ALTER TABLE questions ALTER COLUMN id RESTART WITH 2;
ALTER TABLE answer_options ALTER COLUMN id RESTART WITH 5;