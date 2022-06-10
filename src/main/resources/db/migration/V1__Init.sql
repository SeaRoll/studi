CREATE TABLE users (
    id serial PRIMARY KEY,
    email VARCHAR (255) UNIQUE NOT NULL,
    hashedPassword VARCHAR (50) NOT NULL,
    resetToken VARCHAR (255) NOT NULL,
    resetTokenExpirationDate TIMESTAMP NOT NULL
);