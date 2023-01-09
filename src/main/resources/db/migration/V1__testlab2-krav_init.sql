create table Standard (
    id serial primary key,
    tittel varchar not null,
    status varchar not null
);

create table Krav (
    id serial primary key,
    tittel varchar not null,
    status varchar not null,
    innhold varchar
);

create table StandardKrav (
  idStandard int references Standard(id),
  idKrav int references Krav(id)
);

create table Wcag2Krav (
    id serial primary key,
    suksessKriterium varchar not null,
    prinsippp varchar not null,
    retningslinje varchar not null,
    samsvarsnivaa varchar not null,
    idKrav int references Krav(id)
);

create table Regelverk (
    id serial primary key,
    namn varchar not null,
    type varchar not null,
    paragraf varchar,
    virkeomraader varchar
);

create table Tolking (
    id serial primary key,
    idKrav int references Krav(id),
    sistOppdatert date not null,
    innhald varchar not null
);

create table Paragraf (
    id serial primary key,
    nr int not null,
    namn varchar not null,
    gjeldOffSektor boolean,
    gjeldPrivatSektor boolean
);

create table RegelverkParagraf (
    idRegelverk int references Regelverk(id),
    idParagraf int references Standard(id)
);

create table FunksjonellYtelsesevne (
    id serial primary key,
    tittel varchar not null
);

create table KravFunksjonellYtelsesevne (
    idKrav int references Krav(id),
    idFunkjsonellYtelsesevne int references FunksjonellYtelsesevne(id)
);