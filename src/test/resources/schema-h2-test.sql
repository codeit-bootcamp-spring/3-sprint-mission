CREATE SCHEMA IF NOT EXISTS DISCODEIT;
SET SCHEMA DISCODEIT;

DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS user_statuses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS binary_contents;
DROP TABLE IF EXISTS channels;

CREATE TABLE IF NOT EXISTS binary_contents (
    id            UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    file_name     VARCHAR(255)    NOT NULL,
    size          BIGINT          NOT NULL,
    content_type  VARCHAR(100)    NOT NULL,
    extensions    VARCHAR(20)     NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username      VARCHAR(50)     NOT NULL UNIQUE,
    email         VARCHAR(100)    NOT NULL UNIQUE,
    password      VARCHAR(60)     NOT NULL,
    profile_id    UUID,
    CONSTRAINT fk_profile
    FOREIGN KEY (profile_id)
    REFERENCES binary_contents(id)
    ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS user_statuses (
    id             UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id        UUID        NOT NULL UNIQUE,
    last_active_at TIMESTAMP with time zone NOT NULL,
    CONSTRAINT fk_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS channels (
    id           UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    name         VARCHAR(100),
    description  VARCHAR(500),
    type         VARCHAR(10) NOT NULL
    CHECK (type IN ('PUBLIC','PRIVATE'))
    );

CREATE TABLE IF NOT EXISTS read_statuses (
    id           UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id      UUID,
    channel_id   UUID,
    last_read_at TIMESTAMP with time zone ,
    CONSTRAINT fk_rs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE, CONSTRAINT uq_user_channel UNIQUE(user_id, channel_id)
    );

CREATE TABLE IF NOT EXISTS messages (
    id           UUID PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content      TEXT,
    channel_id   UUID             NOT NULL,
    author_id    UUID,
    CONSTRAINT fk_msg_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS message_attachments (
    message_id    UUID,
    attachment_id UUID,
    CONSTRAINT fk_ma_msg FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_ma_attach FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
    );
