-- 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID,
    created_at   TIMESTAMP with time zone NOT NULL,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id         UUID,
    created_at TIMESTAMP with time zone NOT NULL,
    updated_at TIMESTAMP with time zone,
    username   VARCHAR(50)              NOT NULL UNIQUE,
    email      VARCHAR(100)             NOT NULL UNIQUE,
    password   VARCHAR(60)              NOT NULL,
    profile_id UUID,
    PRIMARY KEY (id),
    FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id             UUID,
    created_at     TIMESTAMP with time zone NOT NULL,
    updated_at     TIMESTAMP with time zone,
    user_id        UUID                     NOT NULL UNIQUE,
    last_active_at TIMESTAMP with time zone NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels
(
    id          UUID,
    created_at  TIMESTAMP with time zone NOT NULL,
    updated_at  TIMESTAMP with time zone,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(20)              NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT channel_type_check CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS messages
(
    id         UUID,
    created_at TIMESTAMP with time zone NOT NULL,
    updated_at TIMESTAMP with time zone,
    content    TEXT,
    channel_id UUID,
    author_id  UUID,
    PRIMARY KEY (id),
    FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id           UUID,
    created_at   TIMESTAMP with time zone NOT NULL,
    updated_at   TIMESTAMP with time zone,
    user_id      UUID,
    channel_id   UUID,
    last_read_at TIMESTAMP with time zone NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_read_statuses UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id),
    FOREIGN KEY (message_id)
        REFERENCES messages (id)
        ON DELETE CASCADE,
    FOREIGN KEY (attachment_id)
        REFERENCES binary_contents (id)
        ON DELETE CASCADE
);