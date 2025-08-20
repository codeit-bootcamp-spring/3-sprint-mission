-- 2. 테이블 생성
CREATE TABLE binary_contents (
    id              UUID PRIMARY KEY,
    created_at      timestamp with time zone NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    size            BIGINT NOT NULL,
    content_type    VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id          UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(60) NOT NULL,
    profile_id  UUID,
    role        varchar(20) NOT NULL DEFAULT 'USER',

    CONSTRAINT fk_users_profile
        FOREIGN KEY (profile_id)
        REFERENCES binary_contents(id)
        ON DELETE SET NULL
);

CREATE TABLE persistent_logins (
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMPTZ NOT NULL
);

CREATE TABLE channels (
    id          UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        VARCHAR(100),
    description VARCHAR(500),
    type        VARCHAR(10) NOT NULL

    CONSTRAINT chk_channel_type
        CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE messages (
    id          UUID PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    content     TEXT,
    channel_id  UUID NOT NULL,
    author_id   UUID,

    CONSTRAINT fk_messages_channel
        FOREIGN KEY (channel_id)
        REFERENCES  channels(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
        REFERENCES  users(id)
        ON DELETE SET NULL
);

CREATE TABLE read_statuses (
    id              UUID PRIMARY KEY,
    created_at      timestamp with time zone NOT NULL,
    updated_at      timestamp with time zone,
    user_id         UUID NOT NULL,
    channel_id      UUID NOT NULL,
    last_read_at    TIMESTAMP NOT NULL,

    CONSTRAINT fk_read_statuses_user
        FOREIGN KEY (user_id)
        REFERENCES  users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_read_statuses_channel
        FOREIGN KEY (channel_id)
        REFERENCES  channels(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE message_attachments (
    message_id      UUID NOT NULL,
    attachment_id   UUID NOT NULL,

    CONSTRAINT pk_message_attachments PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_msg_att_message
        FOREIGN KEY (message_id)
        REFERENCES  messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_msg_att_binary
        FOREIGN KEY (attachment_id)
        REFERENCES  binary_contents(id)
        ON DELETE CASCADE
);