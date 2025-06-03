-- 테이블 드롭
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;

-- 테이블 생성
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID
);

CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    channel_id UUID        NOT NULL,
    author_id  UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    content    TEXT
);

CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    last_read_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_statuses
(
    id             UUID PRIMARY KEY,
    user_id        UUID        NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL,
    updated_at     TIMESTAMPTZ,
    last_active_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,
    PRIMARY KEY (message_id, attachment_id)
);

-- FK 제약 추가
ALTER TABLE users
    ADD CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_channel FOREIGN KEY (channel_id) REFERENCES channels (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (id);
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_channel FOREIGN KEY (channel_id) REFERENCES channels (id);
ALTER TABLE user_statuses
    ADD CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_attachment_message FOREIGN KEY (message_id) REFERENCES messages (id);
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_attachment_file FOREIGN KEY (attachment_id) REFERENCES binary_contents (id);