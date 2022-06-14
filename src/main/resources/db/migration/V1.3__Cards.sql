CREATE TABLE cards (
      id serial PRIMARY KEY,
      question TEXT NOT NULL,
      answer TEXT NOT NULL,
      due_date TIMESTAMP NOT NULL,
      subject_id serial REFERENCES subjects(id) ON DELETE CASCADE
);

ALTER TABLE subjects DROP CONSTRAINT subjects_name_key;