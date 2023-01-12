insert into testlab2_krav.krav (tittel, status, innhald, gjeldautomat, gjeldnettsider, gjeldapp, urlrettleiing) values ('1.1.1 Ikke-tekstlig innhold (Nivå A)', 'Gjeldande', 'Suksesskriterium på nivå A (må-krav). Alt ikke-tekstlig innhold som presenteres for brukeren, har et tekstalternativ som har samme formål, med unntak av situasjonene som er beskrevet nedenfor', true, false, true, 'https://www.uutilsynet.no/wcag-standarden/111-ikke-tekstlig-innhold-niva/87');

insert into testlab2_krav.wcag2krav (suksesskriterium, prinsippp, retningslinje, samsvarsnivaa) values ('1.1.1', '1. Mulig å oppfatte', '1.2 Tidsbaserte medier', 'A');

insert into testlab2_krav.paragraf (nr, namn, gjeldoffsektor, gjeldprivatsektor) values ('§ 4b.', 'Krav til universell utforming av nettløysingar i offentlige verksemder.', true, false);

insert into testlab2_krav.standard (tittel, status) values ('Wcag 2.1', 'Gjeldande');

insert into testlab2_krav.regelverk (namn, type) values ('Forskrift om universell utforming av informasjons- og kommunikasjonsteknologiske (IKT)-løsninger', 'forskrift');

insert into testlab2_krav.funksjonellytelsesevne (tittel) values ('Bruk utan syn.');

insert into testlab2_krav.kravtolking (kravid, versjon, formaal, tolkingogpresiseringavkrav, sistoppdatert, innhald, status, dekkaavactreglar, lenkeractreglar) values (1, '1.2', 'Kravet skal gjerde det mulig for personar som treng hjelpmiddel å', 'Kravet gjelder først og fremst for lenka bilete', TO_TIMESTAMP('2022-01-01 13:10:11', 'YYYY-MM-DD HH24:MI:SS'), '<h2>Krav til samsvar</h2><p>Du må bla bla bla for å oppnå samsvar.</p><h2>Testelement</h2><p>Testlement som er relevante for dette kravet er….</p>', 'Ny', true, '{"actReglar": [{"tittel":"act1", "url":"https://act.com/r1"},{"tittel":"act2", "url":"https://act.com/r2"}]}');
