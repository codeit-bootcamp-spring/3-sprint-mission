-- 1. 테이블 삭제
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;


-- 2. 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents
(
    id uuid PRIMARY KEY ,
    created_at timestamptz NOT NULL,
    file_name varchar(255) NOT NULL,
    size bigint NOT NULL ,
    content_type varchar(100) NOT NULL ,
    bytes bytea NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id uuid PRIMARY KEY ,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    username varchar(50) UNIQUE NOT NULL ,
    email varchar(100) UNIQUE NOT NULL ,
    password varchar(60) NOT NULL,
    profile_id uuid,

    -- 제약조건
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS user_statuses
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL,

    --제약조건
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE channels
(
    id UUID PRIMARY KEY,
    created_at timestamptz NOT NULL ,
    updated_at timestamptz,
    name varchar(100),
    description varchar(500),
    type varchar(10) check (type in('PUBLIC','PRIVATE'))
);

CREATE TABLE messages
(
    id UUID PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content text,
    channel_id uuid NOT NULL,
    author_id uuid,

    --제약조건
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id uuid PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id uuid NOT NULL,
    channel_id uuid NOT NULL,
    last_read_at timestamptz not null,

    --제약조건
    UNIQUE(user_id,channel_id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE message_attachments
(
    message_id uuid,
    attachment_id uuid,

    --제약조건
    CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
)

