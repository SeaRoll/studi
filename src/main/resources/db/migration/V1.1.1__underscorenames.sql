ALTER TABLE users RENAME COLUMN hashedpassword to hashed_password;
ALTER TABLE users RENAME COLUMN resettoken to reset_token;
ALTER TABLE users RENAME COLUMN resettokenexpirationdate to reset_token_expiration_timestamp;


