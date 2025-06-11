CREATE SCHEMA IF NOT EXISTS discodeit;

-- DROP TABLE IF EXISTS users CASCADE;
-- DROP TABLE IF EXISTS channels CASCADE;
-- DROP TABLE IF EXISTS messages CASCADE;
-- DROP TABLE IF EXISTS binary_contents cascade;
-- DROP TABLE IF EXISTS user_statuses CASCADE;
-- DROP TABLE IF EXISTS message_attachments CASCADE;

-- binary_contents
create table if not exists binary_contents
(
    id           uuid primary key,
    created_at   timestamptz  not null,
    file_name    varchar(255) not null,
    size         bigint       not null,
    content_type varchar(100) not null
);

create table if not exists users
(
    id         uuid primary key,
    created_at timestamptz  not null,
    updated_at timestamptz,
    username   varchar(50)  not null unique,
    email      varchar(100) not null unique,
    password   varchar(60)  not null,
    profile_id uuid,

    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL

);

create table if not exists channels
(
    id          uuid primary key,
    created_at  timestamptz not null,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) not null check ( type IN ('PUBLIC', 'PRIVATE'))
);

-- messages
create table if not exists messages
(
    id         uuid primary key,
    created_at timestamptz not null,
    updated_at timestamptz,
    content    text,
    channel_id uuid        not null,
    author_id  uuid,

    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL

);

-- user_statuses
create table if not exists user_statuses
(
    id             uuid primary key,
    created_at     timestamptz not null,
    updated_at     timestamptz,
    user_id        uuid        not null,
    last_active_at timestamptz not null,

    CONSTRAINT fk_user_statuses_userId FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- read_statuses
create table if not exists read_statuses
(
    id           uuid primary key,
    created_at   timestamptz not null,
    updated_at   timestamptz,
    user_id      uuid,
    channel_id   uuid,
    last_read_at timestamptz not null,

    CONSTRAINT fk_read_statuses_userId FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channelId FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uq_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

-- message_attachments
create table if not exists message_attachments
(
    message_id    uuid,
    attachment_id uuid,

    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_attachments_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachments_attachment FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

