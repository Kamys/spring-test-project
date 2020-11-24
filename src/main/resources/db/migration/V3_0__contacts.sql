create table contacts
(
    id   serial not null,
    phone text,
    email text
);

create unique index contacts_id_uindex
    on contacts (id);

alter table authors
    add contact_id int;

create unique index authors_contact_id_uindex
    on authors (contact_id);

alter table authors
    add constraint authors_contacts_id_fk
        foreign key (contact_id) references contacts (id);

