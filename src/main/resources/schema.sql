drop table if exists users, requests, items, bookings, booking_statuses, comments;

create table if not exists users (
id serial not null primary key,
name varchar(100) not null,
email varchar(100) not null unique
);

create table if not exists requests (
id serial not null primary key,
description varchar(255),
requester_id bigint not null references users(id) on delete cascade,
created timestamp without time zone not null
);

create table if not exists items (
id serial not null primary key,
name varchar(255) not null,
description varchar(255),
is_available boolean,
owner_id bigint not null references users(id) on delete cascade,
request_id bigint references requests(id) on delete cascade
);

create table if not exists booking_statuses (
id int not null primary key,
name varchar(8) not null unique
);

create table if not exists bookings (
id serial not null primary key,
start_date timestamp without time zone not null,
end_date timestamp without time zone not null,
item_id bigint not null references items(id) on delete cascade,
booker_id bigint not null references users(id) on delete cascade,
status int not null references booking_statuses(id) on delete cascade
);

create table if not exists comments (
id serial not null primary key,
text varchar(255) not null,
item_id bigint not null references items(id) on delete cascade,
author_id bigint not null references users(id) on delete cascade,
created timestamp without time zone not null
);