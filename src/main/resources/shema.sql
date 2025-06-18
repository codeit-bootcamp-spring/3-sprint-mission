-- binary_contents Table
CREATE TABLE binary_contents
(
    -- Primary Key
    id           uuid PRIMARY KEY,

    -- Column
    created_at   timestamptz  NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint       NOT NULL,
    content_type varchar(100) NOT NULL,
    bytes        bytea        NOT NULL
);


-- users Table
CREATE TABLE users
(
    -- Primary Key
    id         uuid PRIMARY KEY,

    -- Column
    created_at timestamptz  NOT NULL,
    updated_at timestamptz,
    username   varchar(50)  NOT NULL,
    email      varchar(100) NOT NULL,
    password   varchar(60)  NOT NULL,
    profile_id uuid,

    -- Unique Key( username, email )
    CONSTRAINT uq_username UNIQUE (username),
    CONSTRAINT uq_email UNIQUE (email),

    -- Foreign Key( profile_ id )
    CONSTRAINT fk_profile
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL
);


-- user_statuses Table
CREATE TABLE user_statuses
(
    -- Primary Key
    id             uuid PRIMARY KEY,

    -- Column
    created_at     timestamptz NOT NULL,
    updated_at     timestamptz,
    user_id        uuid        NOT NULL,
    last_active_at timestamptz NOT NULL,

    -- Unique Key ( user_id )
    CONSTRAINT uq_user_id UNIQUE (user_id),

    -- Foreign Key ( user_id )
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);


-- channels Table
CREATE TABLE channels
(
    -- Primary Key
    id          uuid PRIMARY KEY,

    -- Column
    created_at  timestamptz NOT NULL,
    updated_at  timestamptz,
    name        varchar(100),
    description varchar(500),
    type        varchar(10) NOT NULL,

    -- ENUM ( type )
    -- CHECK () : 칼럼 저장 값 조건 명시, 해당 조건 만족 시에만 값 저장 제한
    CONSTRAINT channel_type CHECK ( type IN ('PUBLIC', 'PRIVATE'))
);


-- read_statuses Table
CREATE TABLE read_statuses
(
    -- Primary Key
    id           uuid PRIMARY KEY,

    -- Column
    created_at   timestamptz NOT NULL,
    updated_at   timestamptz,
    user_id      uuid,
    channel_id   uuid,
    last_read_at timestamptz NOT NULL,

    -- Unique Key( user_id, channel_id )
    CONSTRAINT uq_read_user_channel UNIQUE (user_id, channel_id),

    -- Foreign Key( user_id, channel_id )
    -- user_id
    CONSTRAINT fk_read_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    -- channel_id
    CONSTRAINT fk_read_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE
);


-- messages Table
CREATE TABLE messages
(
    -- Primary Key
    id         uuid PRIMARY KEY,

    -- Column
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    content    text,
    channel_id uuid        NOT NULL,
    author_id  uuid,

    -- Foreign Key( channel_id, author_id )
    -- channel_id
    CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE,
    -- author_id
    CONSTRAINT fk_message_author
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL
);


-- message_attachments Table
CREATE TABLE message_attachments
(
    -- Column
    message_id    uuid,
    attachment_id uuid,

    -- Primary Key( 복합키? ) << 생성하는게 좋을까?
    CONSTRAINT pk_message_attachment PRIMARY KEY (message_id, attachment_id),

    -- Foreign Key( message_id, attachment_id )
    -- message_id
    CONSTRAINT fk_attachment_message
        FOREIGN KEY (message_id)
            REFERENCES messages (id)
            ON DELETE CASCADE,
    -- attachment_id
    CONSTRAINT fk_attachment_binary
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE
);