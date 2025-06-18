DROP TABLE IF EXISTS user_statuses;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS binary_contents;
DROP TABLE IF EXISTS channels;



CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID,
    created_at timestamp with time zone NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
--     bytes        BYTEA        NOT NULL,
    extensions   VARCHAR(20)  NOT NULL,

    CONSTRAINT pk_binary_contents PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id        UUID UNIQUE NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_user_statuses PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) NOT NULL check ( type IN ('PUBLIC', 'PRIVATE')),

    CONSTRAINT pk_channels PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    user_id      UUID,
    channel_id   UUID,
    last_read_at TIMESTAMPTZ,

    CONSTRAINT pk_read_statuses PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    TEXT,
    channel_id UUID        NOT NULL,
    author_id  UUID,

    CONSTRAINT pk_messages PRIMARY KEY (id),
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL

);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID,
    attachment_id UUID,

    CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);