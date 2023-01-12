create table Krav (
    id serial primary key,
    tittel varchar not null,
    status varchar not null,
    innhald varchar,
    gjeldAutomat boolean,
    gjeldNettsider boolean,
    gjeldApp boolean,
    urlRettleiing varchar
);

create table Wcag2Krav (
    id serial primary key,
    suksessKriterium varchar not null,
    prinsippp varchar not null,
    retningslinje varchar not null,
    samsvarsnivaa varchar not null
);

create table Paragraf (
    id serial primary key,
    nr varchar not null,
    namn varchar not null,
    gjeldOffSektor boolean,
    gjeldPrivatSektor boolean
);

create table Standard (
    id serial primary key,
    tittel varchar not null,
    status varchar not null
);

create table Regelverk (
    id serial primary key,
    namn varchar not null,
    type varchar not null
);

create table FunksjonellYtelsesevne (
    id serial primary key,
    tittel varchar not null
);

create table KravTolking (
     id serial primary key,
     kravId int references Krav(id),
     versjon varchar not null,
     formaal varchar not null,
     tolkingOgPresiseringAvKrav varchar not null,
     sistOppdatert date not null,
     innhald text,
     status varchar,
     dekkaAvACTReglar boolean,
     lenkerACTReglar json not null
);