-- 테이블 생성
-- binary_contents
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

-- binary_contents
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

-- users
CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ         NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50) UNIQUE  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(60)         NOT NULL,
    profile_id UUID,

    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id)
        ON DELETE SET NULL
);

-- user_statuses
CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ,
    user_id        UUID UNIQUE NOT NULL,
    last_active_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- channels
--DROP TYPE IF EXISTS channel_type CASCADE;
--CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

-- read_statuses
CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID,
    channel_id   UUID,
    last_read_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_user_channel UNIQUE (user_id, channel_id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE
);

-- messages
CREATE TABLE IF NOT EXISTS messages
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    content    TEXT,
    channel_id UUID        NOT NULL,
    author_id  UUID,

    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

-- message_attachments
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID,
    attachment_id UUID,

    CONSTRAINT fk_message_id FOREIGN KEY (message_id)
        REFERENCES messages (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id)
        REFERENCES binary_contents (id)
        ON DELETE CASCADE
);

