create table if not exists checks
(

    id              bigint auto_increment primary key,
    checked_on      date         not null,
    checked_in_at   time         not null,
    checked_out_at  time         null,
    current_session varchar(255) null,
    is_active       bit          not null default 1,
    lat             float        null,
    lon             float        null,
    valid_location  bit          not null default 0,
    pincode         int          null,
    qrcode          varchar(255) not null

) charset = utf8;

create table if not exists users
(

    id              bigint auto_increment primary key,
    first_name      varchar(255)                null,
    last_name       varchar(255)                null,
    username        varchar(255)                not null,
    email           varchar(255)                not null,
    phone           varchar(255)                null,
    hashed_password varchar(255)                not null,
    profile         longtext                    null,
    registered_on   date                        null,
    registered_at   time                        null,
    updated_at      time                        null,
    roles           varchar(100) default 'USER' null,

    constraint email unique (email),
    constraint phone unique (phone),
    constraint username unique (username)

) charset = utf8;

create table if not exists courses
(

    id          bigint auto_increment primary key,
    title       varchar(255)  null,
    description varchar(2000) null,
    parent_id   bigint        null,

    constraint title unique (title),

    constraint parent_to_child_fk
        foreign key (parent_id) references courses (id)

) charset = utf8;

create table if not exists events
(

    id           bigint auto_increment primary key,
    check_id     bigint             not null,
    course_id    bigint             not null,
    attendee_id  bigint             not null,
    organizer_id bigint             null,
    check_type   enum ('IN', 'OUT') not null,

    constraint check_to_attendee_fk
        foreign key (check_id) references checks (id),

    constraint course_to_event_fk
        foreign key (course_id) references courses (id),

    constraint user_to_attendee_fk
        foreign key (attendee_id) references users (id),

    constraint user_to_organizer_fk
        foreign key (organizer_id) references users (id)

) charset = utf8;

