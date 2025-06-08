-- binary_contents 테이블
CREATE TABLE binary_contents (
                                 id UUID PRIMARY KEY,
                                 created_at TIMESTAMPTZ NOT NULL,
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(100) NOT NULL,
                                 bytes BYTEA NOT NULL
);

-- users 테이블
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       created_at TIMESTAMPTZ NOT NULL,
                       updated_at TIMESTAMPTZ,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL,
                       profile_id UUID,
                       CONSTRAINT fk_users_profile_id FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id)
                           ON DELETE SET NULL
);

-- ENUM 타입 생성 (channels.type용)
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'channel_type') THEN
        CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');
    END IF;
END $$;

-- channels 테이블
CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          name VARCHAR(100) NOT NULL,
                          description VARCHAR(500),
                          type channel_type NOT NULL
);

-- user_statuses 테이블
CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL UNIQUE,
                               last_active_at TIMESTAMPTZ NOT NULL,
                               CONSTRAINT fk_user_statuses_user_id FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE
);

-- read_statuses 테이블
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               created_at TIMESTAMPTZ NOT NULL,
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ,
                               UNIQUE (user_id, channel_id),
                               CONSTRAINT fk_read_statuses_user_id FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT fk_read_statuses_channel_id FOREIGN KEY (channel_id)
                                   REFERENCES channels(id)
                                   ON DELETE CASCADE
);

-- messages 테이블
CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          created_at TIMESTAMPTZ NOT NULL,
                          updated_at TIMESTAMPTZ,
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_messages_channel_id FOREIGN KEY (channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,
                          CONSTRAINT fk_messages_author_id FOREIGN KEY (author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

-- message_attachments 테이블
CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,
                                     PRIMARY KEY (message_id, attachment_id),
                                     CONSTRAINT fk_message_attachments_message_id FOREIGN KEY (message_id)
                                         REFERENCES messages(id)
                                         ON DELETE CASCADE,
                                     CONSTRAINT fk_message_attachments_attachment_id FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id)
                                         ON DELETE CASCADE
);