create table todo
        (
        id           varchar(36) primary key,
        date_created timestamp not null default now(),
        done         boolean   not null default false,
        task         varchar(255)
        );