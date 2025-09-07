DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    status       VARCHAR(20)  NOT NULL CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAIL'))
);

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ         NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID                REFERENCES binary_contents (id) ON DELETE SET NULL,
    role       varchar(20)         NOT NULL
);

CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    content    TEXT,
    channel_id UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    author_id  UUID        REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE read_statuses
(
    id                   UUID PRIMARY KEY,
    created_at           TIMESTAMPTZ NOT NULL,
    updated_at           TIMESTAMPTZ,
    user_id              UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_id           UUID        NOT NULL REFERENCES channels (id) ON DELETE CASCADE,
    last_read_at         TIMESTAMPTZ NOT NULL,
    notification_enabled boolean     NOT NULL,
    UNIQUE (user_id, channel_id)
);

CREATE TABLE message_attachments
(
    message_id    UUID REFERENCES messages (id) ON DELETE CASCADE,
    attachment_id UUID REFERENCES binary_contents (id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE notifications
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    receiver_id uuid                     NOT NULL,
    title       varchar(255)             NOT NULL,
    content     text                     NOT NULL
);