--0. create schema and database
--*운영용 DB
CREATE USER discodeit_user
    PASSWORD 'discodeit1234'
    CREATEDB;

drop database discodeit;
create database discodeit
    WITH
    OWNER = discodeit_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf-8'
    LC_CTYPE = 'en_US.utf-8'
    TEMPLATE template0;

create schema discodeit;

GRANT ALL PRIVILEGES ON DATABASE discodeit TO discodeit_user;
GRANT USAGE ON SCHEMA discodeit TO discodeit_user;
GRANT ALL ON ALL TABLES IN SCHEMA discodeit TO discodeit_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA discodeit TO discodeit_user;

--* 개발용 DB
CREATE USER dev_user
    PASSWORD 'dev_1234'
    CREATEDB;

create database discodeit_dev
    WITH
    OWNER = dev_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf-8'
    LC_CTYPE = 'en_US.utf-8'
    TEMPLATE template0;

GRANT ALL PRIVILEGES ON DATABASE discodeit TO dev_user;
GRANT USAGE ON SCHEMA discodeit TO dev_user;
GRANT ALL ON ALL TABLES IN SCHEMA discodeit TO dev_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA discodeit TO dev_user;

create schema discodeit;

\c discodeit postgres;
\c discodeit discodeit_user
SET SCHEMA 'discodeit';

\c discodeit_dev dev_user
SET SCHEMA 'discodeit';

DROP TABLE IF EXISTS user_statuses;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS binary_contents;
DROP TYPE IF EXISTS channel_type;

-- 1. create tables
-- User
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username   varchar(50) UNIQUE       NOT NULL,
    email      varchar(100) UNIQUE      NOT NULL,
    password   varchar(60)              NOT NULL,
    profile_id uuid
);

-- BinaryContent
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL
);

-- UserStatus
CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamp with time zone NOT NULL,
    updated_at     timestamp with time zone,
    user_id        uuid UNIQUE              NOT NULL,
    last_active_at timestamptz              NOT NULL
);

-- Channel
CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)              NOT NULL
);

-- Message
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    text,
    channel_id uuid                     NOT NULL,
    author_id  uuid
);

-- Message.attachments
CREATE TABLE message_attachments
(
    message_id    uuid,
    attachment_id uuid,
    PRIMARY KEY (message_id, attachment_id)
);

-- ReadStatus
CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    user_id      uuid                     NOT NULL,
    channel_id   uuid                     NOT NULL,
    last_read_at timestamptz              NOT NULL,
    UNIQUE (user_id, channel_id)
);

-- ALTER TABLE binary_contents
--     DROP COLUMN bytes;


commit;
