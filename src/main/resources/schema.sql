-- ALTER TABLE users
--     ADD role varchar(20) NOT NULL default 'USER';
-- ALTER TABLE binary_contents
--     ADD COLUMN updated_at timestamp with time zone;
-- ALTER TABLE binary_contents
--     ADD COLUMN status varchar(20) NOT NULL DEFAULT 'SUCCESS';
-- ALTER TABLE read_statuses
--     ADD COLUMN notification_enabled boolean NOT NULL default true;

-- UPDATE read_statuses rs
-- SET notification_enabled = (c.type = 'PRIVATE')
-- FROM channels c
-- WHERE rs.channel_id = c.id
--   AND rs.notification_enabled IS DISTINCT FROM (c.type = 'PRIVATE');

-- INSERT INTO notifications (id, created_at, receiver_id, title, content)
-- VALUES (gen_random_uuid(),
--         now(),
--         '6d6a6a8a-93f5-4694-8bcf-bac206fb8693',
--         '연습 알림',
--         '이것은 연습용 알림입니다.')
-- RETURNING *;

-- drop all tables
DROP TABLE IF EXISTS binary_contents CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_statuses CASCADE;
DROP TABLE IF EXISTS channels CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS read_statuses CASCADE;
DROP TABLE IF EXISTS message_attachments CASCADE;

-- binary_contents
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL,
    status       varchar(20)              NOT NULL
);

-- users
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username   varchar(50) UNIQUE       NOT NULL,
    email      varchar(100) UNIQUE      NOT NULL,
    password   varchar(60)              NOT NULL,
    profile_id uuid,
    role       varchar(20)              NOT NULL,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id)
        REFERENCES binary_contents (id) ON DELETE SET NULL
);

-- user_statuses
CREATE TABLE user_statuses
(
    id             uuid PRIMARY KEY,
    created_at     timestamp with time zone NOT NULL,
    updated_at     timestamp with time zone,
    user_id        uuid UNIQUE              NOT NULL,
    last_active_at timestamp with time zone NOT NULL,
    CONSTRAINT fk_user_status_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- channels
CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)              NOT NULL CHECK ( type IN ('PUBLIC', 'PRIVATE') )
);

-- messages
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    text,
    channel_id uuid                     NOT NULL,
    author_id  uuid, -- 유저가 사라져도 메세지는 남기겠다는 의미
    CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id)
        REFERENCES channels (id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_author FOREIGN KEY (author_id)
        REFERENCES users (id) ON DELETE SET NULL
);

-- read_statuses
CREATE TABLE read_statuses
(
    id                   uuid PRIMARY KEY,
    created_at           timestamp with time zone NOT NULL,
    updated_at           timestamp with time zone,
    user_id              uuid                     NOT NULL,
    channel_id           uuid                     NOT NULL,
    last_read_at         timestamp with time zone NOT NULL,
    notification_enabled boolean                  NOT NULL,
    CONSTRAINT uk_read_status UNIQUE (user_id, channel_id),
    CONSTRAINT fk_read_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_read_channel FOREIGN KEY (channel_id)
        REFERENCES channels (id) ON DELETE CASCADE
);

--message_attachments
CREATE TABLE message_attachments
(
    message_id    uuid NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (message_id, attachment_id),
    CONSTRAINT fk_attachment_message FOREIGN KEY (message_id)
        REFERENCES messages (id) ON DELETE CASCADE,
    CONSTRAINT fk_attachment_image FOREIGN KEY (attachment_id)
        REFERENCES binary_contents (id) ON DELETE CASCADE
);

-- notifications
CREATE TABLE notifications
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    receiver_id uuid                     NOT NULL,
    title       varchar(120)             NOT NULL,
    content     text                     NOT NULL
);