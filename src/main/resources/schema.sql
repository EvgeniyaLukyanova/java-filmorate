CREATE TABLE IF NOT EXISTS "films" (
  "id" integer generated by default as identity PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "release" date,
  "duration" integer,
  "rate" integer,
  "mpa_id" integer
);

CREATE TABLE IF NOT EXISTS "users" (
  "id" integer generated by default as identity PRIMARY KEY,
  "email" varchar(255) NOT NULL,
  "login" varchar(50) NOT NULL,
  "name" varchar,
  "birthday" varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS "likes" (
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE IF NOT EXISTS "friends" (
  "user1_id" integer,
  "user2_id" integer,
  "confirmation" boolean
);

CREATE TABLE IF NOT EXISTS "genres" (
  "id" integer generated by default as identity PRIMARY KEY,
  "name" varchar(255)
);

CREATE TABLE IF NOT EXISTS "film_genres" (
  "id" integer generated by default as identity PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE IF NOT EXISTS "mpa" (
  "id" integer generated by default as identity PRIMARY KEY,
  "name" varchar(5)
);

ALTER TABLE "likes" DROP CONSTRAINT IF EXISTS "fk_film";
ALTER TABLE "likes" ADD CONSTRAINT "fk_film" FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "likes" DROP CONSTRAINT IF EXISTS "fk_user";
ALTER TABLE "likes" ADD CONSTRAINT "fk_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "friends" DROP CONSTRAINT IF EXISTS "fk_user1";
ALTER TABLE "friends" ADD CONSTRAINT "fk_user1" FOREIGN KEY ("user1_id") REFERENCES "users" ("id");

ALTER TABLE "friends" DROP CONSTRAINT IF EXISTS "fk_user2";
ALTER TABLE "friends" ADD CONSTRAINT "fk_user2" FOREIGN KEY ("user2_id") REFERENCES "users" ("id");

ALTER TABLE "film_genres" DROP CONSTRAINT IF EXISTS "fk_g_film";
ALTER TABLE "film_genres" ADD CONSTRAINT "fk_g_film" FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "film_genres" DROP CONSTRAINT IF EXISTS "fk_genre";
ALTER TABLE "film_genres" ADD CONSTRAINT "fk_genre" FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "films" DROP CONSTRAINT IF EXISTS "fk_mpa";
ALTER TABLE "films" ADD CONSTRAINT "fk_mpa" FOREIGN KEY ("mpa_id") REFERENCES "mpa" ("id");

CREATE UNIQUE INDEX IF NOT EXISTS USER_EMAIL_UINDEX on "users" ("email");
CREATE UNIQUE INDEX IF NOT EXISTS USER_LOGIN_UINDEX on "users" ("login");
