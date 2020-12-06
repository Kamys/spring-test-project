alter table reviews
    alter column rating type text using rating::text;