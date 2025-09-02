-- 테이블 드롭
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;

-- 테이블 생성
CREATE TABLE channels
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10)              NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50)              NOT NULL,
    email      VARCHAR(100)             NOT NULL,
    password   VARCHAR(60)              NOT NULL,
    role       VARCHAR(20)              NOT NULL,
    profile_id UUID
);

CREATE TABLE binary_contents
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    status       VARCHAR(20)              NOT NULL CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAIL'))
);

CREATE TABLE messages
(
    id         UUID PRIMARY KEY,
    channel_id UUID                     NOT NULL,
    author_id  UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT
);

CREATE TABLE read_statuses
(
    id           UUID PRIMARY KEY,
    user_id      UUID                     NOT NULL,
    channel_id   UUID                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    notification_enabled BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE message_attachments
(
    message_id    UUID NOT NULL,
    attachment_id UUID NOT NULL,
    PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    receiver_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);

-- FK 제약 추가
ALTER TABLE users
    ADD CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE CASCADE;
ALTER TABLE messages
    ADD CONSTRAINT fk_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE;
ALTER TABLE messages
    ADD CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_channel FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE;
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_attachment_message FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE;
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_attachment_file FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE;
ALTER TABLE notifications
    ADD CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE;