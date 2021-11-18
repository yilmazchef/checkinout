drop table users;

create table if not exists users
(
    id              bigint auto_increment,
    username        varchar(255) not null,
    first_name      varchar(255) null,
    last_name       varchar(255) null,
    phone           varchar(255) null unique,
    email           varchar(255) null unique,
    hashed_password varchar(255) not null,
    profile         longtext     null,
    registered_on   date         null,
    registered_at   time         null,
    updated_at      time         null,

    primary key (id)

) charset = utf8;

drop table checks;

create table if not exists checks
(
    id              bigint auto_increment,
    checked_on      date         null,
    checked_in_at   time         null,
    checked_out_at  time         null,
    current_session varchar(255) null,
    is_active       bit          null,
    lat             float        null,
    lon             float        null,
    pincode         int          null,
    qrcode          varchar(255) null,

    primary key (id)
) charset = utf8;

drop table events;

create table if not exists events
(
    id           bigint auto_increment,
    attendee_id  bigint null,
    organizer_id bigint null,

    primary key (id),

    constraint user_to_attendee_fk
        foreign key (attendee_id) references users (id),
    constraint user_to_organizer_fk
        foreign key (organizer_id) references users (id)
);


