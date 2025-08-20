DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS binary_contents;
DROP TABLE IF EXISTS persistent_logins;

-- User
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50) UNIQUE       NOT NULL,
    email      VARCHAR(100) UNIQUE      NOT NULL,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID,
    role       VARCHAR(20)              NOT NULL
);

-- BinaryContent
CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL
);

-- Channel
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL
);

-- Message
CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    text,
    channel_id UUID                     NOT NULL,
    author_id  UUID
);

-- Message.attachments
CREATE TABLE message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id)
);

-- ReadStatus
CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    user_id      UUID                     NOT NULL,
    channel_id   UUID                     NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (user_id, channel_id)
);

-- Persistent Logins (Spring Security Remember-Me)
CREATE TABLE persistent_logins
(
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);

-- Foreign Keys
ALTER TABLE users
    ADD CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT fk_message_user
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL;

ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_binary_content
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;
