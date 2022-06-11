CREATE TABLE subjects (
   id serial PRIMARY KEY,
   name VARCHAR (255) UNIQUE NOT NULL,
   user_id serial REFERENCES users(id) ON DELETE CASCADE
);