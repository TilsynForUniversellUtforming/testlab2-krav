alter table Wcag2Krav
add column tittel varchar(255) not null default '',
add column status varchar(255) not null default '',
add column innhald varchar(255) not null default '',
add column gjeldautomat boolean not null default false,
add column gjeldapp boolean not null default false,
add column urlrettleiing varchar(255) not null default '';

update Wcag2Krav set tittel = Krav.tittel, status = Krav.status, innhald = Krav.innhald, gjeldautomat = Krav.gjeldautomat, gjeldapp = Krav.gjeldapp, urlrettleiing = Krav.urlrettleiing
from Krav
WHERE Wcag2Krav.kravid = Krav.id;