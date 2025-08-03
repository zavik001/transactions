CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    owner VARCHAR(255),
    balance DECIMAL(19,2)
);