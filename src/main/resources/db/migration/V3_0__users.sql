create table users
(
    id serial not null,
    name text
);

create unique index users_id_uindex
    on users (id);

alter table users
    add constraint users_pk
        primary key (id);

alter table authors drop column name;

alter table authors
    add description_of_written_style text;

alter table authors
    add user_id integer;

create unique index authors_user_id_uindex
    on authors (user_id);

alter table authors
    add constraint authors_users_id_fk
        foreign key (user_id) references users (id);

