-- Drop Tables
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS binary_contents CASCADE;


-- Create Table
-- binary_contents Table
CREATE TABLE binary_contents
(
    -- Primary Key
    id           UUID PRIMARY KEY,

    -- Column
    created_at   timestamp with time zone  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    size         BIGINT       NOT NULL,
    content_type VARCHAR(100) NOT NULL
);

-- users Table
CREATE TABLE users
(
    -- Primary Key
    id         UUID PRIMARY KEY,

    -- Column
    created_at timestamp with time zone  NOT NULL,
    updated_at timestamp with time zone,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,
    role VARCHAR(20) NOT NULL,

    -- Unique Key
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),

    -- Foreign Key
    FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- channels Table
CREATE TABLE channels
(
    -- Primary Key
    id          UUID PRIMARY KEY,

    -- Column
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL
);

-- messages Table
CREATE TABLE messages
(
    -- Primary Key
    id         UUID PRIMARY KEY,

    -- Column
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    TEXT,

    -- Foreign Key
    channel_id UUID        NOT NULL,
    author_id  UUID,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

-- message_attachments Table
CREATE TABLE message_attachments
(
    -- Primary Key (Composite)
    message_id    UUID,
    attachment_id UUID,
    PRIMARY KEY (message_id, attachment_id),

    -- Foreign Key
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- read_statuses Table
CREATE TABLE read_statuses
(
    -- Primary Key
    id           UUID PRIMARY KEY,

    -- Column
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    last_read_at timestamp with time zone NOT NULL,

    -- Foreign Key
    user_id      UUID        NOT NULL,
    channel_id   UUID        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,

    -- Unique Key
    CONSTRAINT uk_read_status_user_channel UNIQUE (user_id, channel_id)
);
