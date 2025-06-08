--0. create schema and database
CREATE USER discodeit_user
PASSWORD 'discodeit1234'
CREATEDB;

drop database discodeit;
create database discodeit
    WITH
    OWNER = ohgiraffers
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf-8'
    LC_CTYPE = 'en_US.utf-8'
    TEMPLATE template0;

\c discodeit

create schema if not exists discodeit;

alter schema discodeit owner to discodeit_user;

ALTER ROLE discodeit_user SET search_path TO discodeit;

SET search_path TO discodeit;

SHOW search_path;

SELECT * FROM discodeit.USERS;

DROP TABLE IF EXISTS user_statuses;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS binary_contents;
DROP TYPE IF EXISTS channel_type;

-- 1. create tables
-- 1) binary_content
DROP TABLE IF EXISTS binary_contents;
CREATE TABLE IF NOT EXISTS binary_contents (
   id UUID,
   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
   file_name VARCHAR(255) NOT NULL,
   size BIGINT NOT NULL,
   content_type VARCHAR(100) NOT NULL,
   bytes BYTEA NOT NULL,
   CONSTRAINT pk_binary_content_id PRIMARY KEY (id)
);

-- 2) user
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users
(
    id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50)       NOT NULL,
    email      VARCHAR(100)      NOT NULL,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT fk_user_profile_id FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL,
    CONSTRAINT uq_username UNIQUE (username),
    CONSTRAINT uq_email UNIQUE (email)
);

-- 2) user_status
DROP TABLE IF EXISTS user_statuses;
CREATE TABLE IF NOT EXISTS user_statuses (
    id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    user_id UUID NOT NULL,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_user_status_id PRIMARY KEY (id),
    CONSTRAINT fk_user_status_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_id UNIQUE (user_id)
);

-- 3) channel
DROP TABLE IF EXISTS channels;
CREATE TABLE IF NOT EXISTS channels (
    id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL,
    CONSTRAINT pk_channel_id PRIMARY KEY (id)
);

-- 4) read_status
DROP TABLE IF EXISTS read_statuses;
CREATE TABLE IF NOT EXISTS read_statuses (
    id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    user_id UUID,
    channel_id UUID,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_read_status_id PRIMARY KEY (id),
    CONSTRAINT fk_read_status_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_status_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uq_read_status_user_channel UNIQUE(user_id, channel_id)
);

-- 5) message
DROP TABLE IF EXISTS messages;
CREATE TABLE IF NOT EXISTS messages (
    id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,
    CONSTRAINT pk_message_id PRIMARY KEY (id),
    CONSTRAINT fk_message_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- message_attachment
DROP TABLE IF EXISTS message_attachments;
CREATE TABLE IF NOT EXISTS message_attachments (
    message_id UUID,
    attachment_id UUID,
    CONSTRAINT fk_message_attachment_message_id FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_message_attachment_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

commit;
