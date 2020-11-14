create table if not exists books
(
    id   serial not null,
    name text
);

create unique index books_id_uindex
    on books (id);

-- TABLE authors
create table if not exists  authors
(
    id            serial not null,
    name          text   not null,
    date_of_birth date
);

create unique index authors_id_uindex
    on authors (id);

