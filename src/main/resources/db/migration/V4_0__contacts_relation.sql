alter table contacts
	add author_id int not null unique;

alter table contacts
	add foreign key (author_id) references authors (id);

alter table authors drop column contact_id;




