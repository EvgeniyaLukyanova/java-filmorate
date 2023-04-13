insert into "genres"("name") select 'Комедия' where not exists(select "id" from "genres" where "name" = 'Комедия');
insert into "genres"("name") select 'Драма' where not exists(select "id" from "genres" where "name" = 'Драма');
insert into "genres"("name") select 'Мультфильм' where not exists(select "id" from "genres" where "name" = 'Мультфильм');
insert into "genres"("name") select 'Триллер' where not exists(select "id" from "genres" where "name" = 'Триллер');
insert into "genres"("name") select 'Документальный' where not exists(select "id" from "genres" where "name" = 'Документальный');
insert into "genres"("name") select 'Боевик' where not exists(select "id" from "genres" where "name" = 'Боевик');

insert into "mpa"("name") select 'G' where not exists(select "id" from "mpa" where "name" = 'G');
insert into "mpa"("name") select 'PG' where not exists(select "id" from "mpa" where "name" = 'PG');
insert into "mpa"("name") select 'PG-13' where not exists(select "id" from "mpa" where "name" = 'PG-13');
insert into "mpa"("name") select 'R' where not exists(select "id" from "mpa" where "name" = 'R');
insert into "mpa"("name") select 'NC-17' where not exists(select "id" from "mpa" where "name" = 'NC-17');