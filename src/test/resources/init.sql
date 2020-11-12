create table if not exists books
(
    id
         serial not null
        constraint books_pk
            primary key,
    name text
);