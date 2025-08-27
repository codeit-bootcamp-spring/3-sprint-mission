/*SELECT
    n.nspname AS schema_name,
    pg_get_userbyid(n.nspowner) AS schema_owner
FROM
    pg_namespace n
WHERE
    n.nspname = 'discodeit';*/


-- 1. 새 유저 'discodeit_user' 생성 (비밀번호는 원하는 값으로 설정)
-- CREATE USER discodeit_user WITH PASSWORD 'discodeit1234';

-- 2. postgres 계정은 AWS RDS 환경 특성상 완전한 super user가 아니므로, discodeit_user에 대한 권한을 추가로 부여해야함.
-- GRANT discodeit_user TO postgres;

-- 3. 'discodeit' 데이터베이스 생성 (소유자는 'discodeit_user')
-- CREATE DATABASE discodeit OWNER discodeit_user;

-- 4. schema.sql 실행하여 테이블 생성
CREATE SCHEMA IF NOT EXISTS discodeit;

GRANT USAGE ON SCHEMA discodeit TO discodeit_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA discodeit TO discodeit_user;

ALTER ROLE discodeit_user SET search_path TO discodeit;

DROP TABLE IF EXISTS tbl_users CASCADE;
DROP TABLE IF EXISTS tbl_channels CASCADE;
DROP TABLE IF EXISTS tbl_binary_contents CASCADE;
DROP TABLE IF EXISTS tbl_read_statuses CASCADE;
DROP TABLE IF EXISTS tbl_messages CASCADE;
DROP TABLE IF EXISTS tbl_message_attachments CASCADE;

CREATE TABLE IF NOT EXISTS discodeit.tbl_binary_contents
(
    id           UUID PRIMARY KEY, --> tbl_users profile_id, tbl_message_attachments attachment_id
    created_at   TIMESTAMP WITH TIME ZONE  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes        BYTEA        NOT NULL
);

-- 실제로 파일 다운로드하기 전까지는 메타 정보만 알고 있으면 되기 때문에 상대적인 성능 향상 위해 DROP
ALTER TABLE discodeit.tbl_binary_contents
    DROP COLUMN bytes;

CREATE TABLE IF NOT EXISTS tbl_users
(
    id         UUID PRIMARY KEY, --> tbl_messages author_id
    created_at TIMESTAMP WITH TIME ZONE  NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES tbl_binary_contents (id) ON DELETE SET NULL
);

-- -- User (1) -> BinaryContent (1)
-- ALTER TABLE tbl_users
--     ADD CONSTRAINT fk_profile_id
--         FOREIGN KEY (profile_id)
--             REFERENCES tbl_binary_contents (id)
--             ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS discodeit.tbl_channels
(
    id          UUID PRIMARY KEY, --> tbl_messages channel_id
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS discodeit.tbl_read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE,
    user_id      UUID NOT NULL,
    channel_id   UUID NOT NULL,
    last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_users (id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES tbl_channels (id) ON DELETE CASCADE,
    UNIQUE (user_id, channel_id)
);

-- -- ReadStatus (N) -> User (1)
-- ALTER TABLE tbl_read_statuses
--     ADD CONSTRAINT fk_user_id
--         FOREIGN KEY (user_id)
--             REFERENCES tbl_users (id)
--             ON DELETE CASCADE;

-- -- ReadStatus (N) -> Channel (1)
-- ALTER TABLE tbl_read_statuses
--     ADD CONSTRAINT fk_channel_id
--         FOREIGN KEY (channel_id)
--             REFERENCES tbl_channels (id)
--             ON DELETE CASCADE;

/*CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');*/

CREATE TABLE IF NOT EXISTS discodeit.tbl_messages
(
    id         UUID PRIMARY KEY, --> tbl_message_attachments message_id
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content    TEXT,
    channel_id UUID NOT NULL,
    author_id  UUID,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES tbl_users (id) ON DELETE SET NULL,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES tbl_channels (id) ON DELETE CASCADE
);

-- -- Message (N) -> Channel (1)
-- ALTER TABLE tbl_messages
--     ADD CONSTRAINT fk_channel_id
--         FOREIGN KEY (channel_id)
--             REFERENCES tbl_channels (id)
--             ON DELETE CASCADE;

-- -- Message (N) -> Author (1)
-- ALTER TABLE tbl_messages
--     ADD CONSTRAINT fK_author_id
--         FOREIGN KEY (author_id)
--             REFERENCES tbl_users (id)
--             ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS discodeit.tbl_message_attachments
(
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id)
--     CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES tbl_messages ON DELETE CASCADE,
--     CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id) REFERENCES tbl_binary_contents ON DELETE CASCADE
);

-- -- MessageAttachment (1) -> BinaryContent (1)
-- ALTER TABLE tbl_message_attachments
--     ADD CONSTRAINT fk_attachment_id
--         FOREIGN KEY (attachment_id)
--             REFERENCES tbl_binary_contents (id)
--             ON DELETE CASCADE;

SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_name = 'tbl_binary_contents';

SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema = 'discodeit'
  AND table_name = 'tbl_binary_contents';
