create table testobjekt
(
    id         serial primary key,
    testobjekt text
);

create table innhaldstype_testing
(
    id           serial primary key,
    innhaldstype text
);

create table tema
(
    id   serial primary key,
    tema text
);


alter table wcag2krav
ADD CONSTRAINT wcag2krav_suksesskriterium_unique UNIQUE (suksesskriterium);

alter table wcag2krav
DROP CONSTRAINT wcag2krav_kravid_fkey;



create table testregel
(
    id                   serial primary key,
    krav                 varchar,
    testregel_schema     varchar                                            not null,
    namn                 text,
    modus                text,
    testregel_id         text,
    versjon              integer                  default 1                 not null,
    status               text                     default 'publisert'::text not null,
    dato_sist_endra      timestamp with time zone default now()             not null,
    spraak               text                     default 'nb'::text        not null,
    tema                 integer references tema,
    type                 text                     default 'nett'::text      not null,
    testobjekt           integer references testobjekt,
    krav_til_samsvar     text,
    innhaldstype_testing integer references innhaldstype_testing,
    krav_id              integer references wcag2krav                not null
);
