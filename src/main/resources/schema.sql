CREATE TABLE WALLET (
                        ID UUID PRIMARY KEY,
                        BALANCE DECIMAL(19, 2) NOT NULL,
                        CREATED_AT TIMESTAMP NOT NULL,
                        VERSION BIGINT
);