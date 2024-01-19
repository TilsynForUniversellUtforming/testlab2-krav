alter table Wcag2Krav
rename column prinsippp to prinsipp;

alter  table Wcag2Krav
add column kravId int references Krav(id) on delete cascade;

