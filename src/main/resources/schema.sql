/*
 ERD를 참고하여 테이블 생성
 */

DROP TABLE IF EXISTS message_attachments;

DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS user_statuses;
DROP TABLE IF EXISTS messages;

DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS binary_contents;


-- binary_contents 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents
(
    id UUID,
    created_at timestamptz NOT NULL,
    file_name varchar(255) NOT NULL,
    size bigint NOT NULL,
    content_type varchar(100) NOT NULL,
--     bytes bytea NOT NULL,

    CONSTRAINT pk_binary_id PRIMARY KEY (id)
);


-- users 테이블 생성
CREATE TABLE IF NOT EXISTS users
(
    id UUID,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    username varchar(50) UNIQUE NOT NULL,
    email varchar(100) UNIQUE NOT NULL,
    password varchar(60) NOT NULL,
    profile_id UUID,

    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);


-- channels 테이블 생성
CREATE TABLE IF NOT EXISTS channels
(
    id UUID,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    name varchar(100),
    description varchar(500),
    type varchar(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE')),

    CONSTRAINT pk_channel_id PRIMARY KEY (id)
);


-- messages 테이블 생성
CREATE TABLE IF NOT EXISTS messages
(
    id UUID,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content text,
    channel_id UUID NOT NULL,
    author_id UUID,

    CONSTRAINT pk_message_id PRIMARY KEY (id),
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);


-- user_statuses 테이블 생성
CREATE TABLE IF NOT EXISTS user_statuses
(
    id UUID,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id UUID UNIQUE NOT NULL,
    last_active_at timestamptz NOT NULL,

    CONSTRAINT pk_user_status_id PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


-- read_statuses 테이블 생성
CREATE TABLE IF NOT EXISTS read_statuses
(
    id UUID,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    user_id UUID NOT NULL,
    channel_id UUID NOT NULL,
    last_read_at timestamptz NOT NULL,

    CONSTRAINT pk_read_status_id PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id)
);


-- message_attachments 테이블 생성
CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id UUID,
    attachment_id UUID,

    CONSTRAINT pk_message_attachments_id PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);