create table books
(
    id   serial not null,
    name text
);

create unique index books_id_uindex
    on books (id);

-- TABLE authors
create table  authors
(
    id            serial not null,
    name          text   not null,
    date_of_birth date
);

create unique index authors_id_uindex
    on authors (id);

--TABLE author_book
create table author_book
(
    book_id   integer
        constraint author_book_books_id_fk
            references books (id),
    author_id integer
        constraint author_book_authors_id_fk
            references authors (id)
);
