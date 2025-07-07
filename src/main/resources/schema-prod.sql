create schema if not exists discodeit authorization discodeit_user;
GRANT USAGE ON SCHEMA discodeit TO discodeit_user;
GRANT create ON SCHEMA discodeit TO discodeit_user;
alter role discodeit_user set search_path to discodeit, public;
set search_path to discodeit, public;

-- 1. ENUM 타입 생성
DO '
DECLARE
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = ''channel_type'') THEN
        CREATE TYPE channel_type AS ENUM (''PUBLIC'', ''PRIVATE'');
    END IF;
END;
';

-- 2. 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    profile_id UUID,

    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(100),
    description VARCHAR(500),
    type channel_type NOT NULL
);

CREATE TABLE IF NOT EXISTS user_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    user_id UUID NOT NULL UNIQUE,
    last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_user_statuses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content TEXT,
    channel_id UUID NOT NULL,
    author_id UUID,

    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS read_statuses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id)
        REFERENCES channels(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS message_attachments (
    message_id UUID NOT NULL,
    attachment_id UUID NOT NULL,

    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_msg_att_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_msg_att_binary
        FOREIGN KEY (attachment_id)
        REFERENCES binary_contents(id)
        ON DELETE CASCADE
);

select * from users;
DELETE FROM users
WHERE username = 'ubin';