-- 1. 스키마 생성
CREATE SCHEMA IF NOT EXISTS discodeit;

-- SET search_path TO discodeit;

-- 2. 스키마 권한 및 앞으로 생성될 객체 자동 권한 설정
-- GRANT USAGE ON SCHEMA discodeit TO discodeit_user;
-- GRANT ALL PRIVILEGES ON SCHEMA discodeit TO discodeit_user;

-- 향후 생성되는 모든 테이블에도 자동 권한
-- ALTER DEFAULT PRIVILEGES IN SCHEMA discodeit
--     GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO discodeit_user;

-- 향후 생성되는 모든 시퀀스도 자동 권한
-- ALTER DEFAULT PRIVILEGES IN SCHEMA discodeit
--     GRANT USAGE, SELECT ON SEQUENCES TO discodeit_user;

-- 3. ROLE 검색 경로 설정
-- ALTER ROLE discodeit_user SET search_path TO discodeit, public;

-- 4. 테이블 삭제
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;

-- 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents
(
    id           UUID,
    created_at   TIMESTAMP with time zone NOT NULL,
    updated_at   TIMESTAMP with time zone NOT NULL,
    file_name    VARCHAR(255)             NOT NULL,
    size         BIGINT                   NOT NULL,
    content_type VARCHAR(100)             NOT NULL,
    status       VARCHAR(20)              NOT NULL CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAIL')),
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
    role       VARCHAR(20)              NOT NULL CHECK ( role IN ('ADMIN', 'CHANNEL_MANAGER', 'USER')),

    PRIMARY KEY (id),
    FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id)
        ON DELETE SET NULL
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
    id                   UUID,
    created_at           TIMESTAMP with time zone NOT NULL,
    updated_at           TIMESTAMP with time zone,
    user_id              UUID,
    channel_id           UUID,
    last_read_at         TIMESTAMP with time zone NOT NULL,
    notification_enabled boolean,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    FOREIGN KEY (channel_id)
        REFERENCES channels (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_read_statuses UNIQUE (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS notifications
(
    id         UUID,
    created_at TIMESTAMP with time zone NOT NULL,
    title      VARCHAR(500),
    content    VARCHAR(500),
    user_id    UUID,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
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