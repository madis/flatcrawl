CREATE TABLE properties(
  id SERIAL PRIMARY KEY NOT NULL,
  title text,
  short_desc text,
  link text,
  rooms integer,
  area float,
  price float,
  external_id integer,
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL);
