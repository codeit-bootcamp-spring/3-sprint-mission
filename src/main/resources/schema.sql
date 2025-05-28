/*SELECT
    n.nspname AS schema_name,
    pg_get_userbyid(n.nspowner) AS schema_owner
FROM
    pg_namespace n
WHERE
    n.nspname = 'discodeit';*/

DROP TABLE IF EXISTS tbl_users CASCADE;
DROP TABLE IF EXISTS tbl_channels CASCADE;
DROP TABLE IF EXISTS tbl_binary_contents CASCADE;
DROP TABLE IF EXISTS tbl_user_statuses CASCADE;
DROP TABLE IF EXISTS tbl_read_statuses CASCADE;
DROP TABLE IF EXISTS tbl_messages CASCADE;
DROP TABLE IF EXISTS tbl_message_attachments CASCADE;

CREATE TABLE IF NOT EXISTS tbl_binary_contents
(
    id UUID PRIMARY KEY, --> tbl_users profile_id, tbl_message_attachments attachment_id
    created_at TIMESTAMPTZ NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_users
(
    -- column level constraints
    id UUID PRIMARY KEY, --> tbl_user_statuses user_id, tbl_messages author_id
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    profile_id UUID,
    -- table level constraints
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES tbl_binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tbl_user_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID UNIQUE  NOT NULL,
    last_active_at TIMESTAMPTZ,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_channels
(
    id           UUID PRIMARY KEY, --> tbl_messages channel_id
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ,
    name VARCHAR(100),
    description VARCHAR(500),
    type    VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS tbl_read_statuses
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ,
    user_id      UUID UNIQUE  NOT NULL,
    channel_id UUID UNIQUE NOT NULL,
    last_read_at TIMESTAMPTZ,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_users (id) ON DELETE CASCADE,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES tbl_channels (id) ON DELETE CASCADE
);

/*CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');*/

CREATE TABLE IF NOT EXISTS tbl_messages
(
    id           UUID PRIMARY KEY, --> tbl_message_attachments message_id
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ,
    content TEXT,
    channel_id UUID UNIQUE NOT NULL,
    author_id UUID,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES tbl_users (id) ON DELETE SET NULL,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES tbl_channels (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tbl_message_attachments
(
    message_id UUID,
    attachment_id UUID,
    CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES tbl_messages ON DELETE CASCADE,
    CONSTRAINT fk_attachment_id FOREIGN KEY (attachment_id) REFERENCES tbl_binary_contents ON DELETE CASCADE
)