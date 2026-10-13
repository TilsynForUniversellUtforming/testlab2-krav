CREATE TABLE standard_krav (
    standard_id INT REFERENCES standard(id),
    krav_id INT REFERENCES wcag2krav(id),
    PRIMARY KEY (standard_id, krav_id)

);

INSERT INTO standard (tittel, status, gjeldOffentlig, gjeldPrivat) VALUES
('WCAG 2.0', 'Gjeldande', TRUE, TRUE),
('WCAG 2.1', 'Gjeldande', TRUE, FALSE),
('WCAG 2.2', 'Ny', FALSE, FALSE);