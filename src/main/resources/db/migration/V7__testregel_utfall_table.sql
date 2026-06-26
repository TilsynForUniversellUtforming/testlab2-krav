CREATE TABLE testregel_utfall (
    id BIGINT PRIMARY KEY,
    testregel_id BIGINT NOT NULL,
    utfall TEXT NOT NULL,
    erDefault BOOLEAN NOT NULL,
    FOREIGN KEY (testregel_id) REFERENCES testregel(id)
);