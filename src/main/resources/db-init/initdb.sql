create database auction;

create schema if not exists auction;

create table if not exists users
(
    id       serial8,
    name     varchar not null,
    surname  varchar not null,
    role     int2    not null,
    username varchar not null,
    password varchar not null,
    primary key (id)
);

create table if not exists ads
(
    id            serial8,
    user_id       int8      not null,
    name          varchar   not null,
    description   varchar   not null,
    start_price   int4      not null,
    status        varchar   not null,
    creation_date timestamp not null,

    timer         int4      not null,
    primary key (id),
    foreign key (user_id) references users (id)
);

create table if not exists images
(
    id    serial8,
    ad_id serial8,
    name  varchar not null,
    primary key (id),
    foreign key (ad_id) references ads (id)
);

create table if not exists users_ads
(
    id        serial8,
    user_id   int8 not null,
    ad_id     int8 not null,
    min_price int4 not null,
    primary key (id),
    foreign key (user_id) references users (id),
    foreign key (ad_id) references ads (id)
);
