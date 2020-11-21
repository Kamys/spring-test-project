create table reviews
(
    id   serial not null,
    text text,
    rating integer,
    book_id integer constraint feedback_books_id_fk
            references books (id)

);

create unique index feedback_id_uindex
    on reviews (id);