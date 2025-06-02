-- UUID 확장 모듈 활성화
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 채널 타입을 위한 ENUM 생성
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

-- binary_contents 테이블 (다른 테이블들이 참조하므로 먼저 생성)
CREATE TABLE binary_contents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    bytes BYTEA NOT NULL
);

-- users 테이블
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    profile_id UUID REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- user_statuses 테이블
CREATE TABLE user_statuses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    last_active_at TIMESTAMPTZ NOT NULL
);

-- channels 테이블
CREATE TABLE channels (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    name VARCHAR(100),
    description VARCHAR(500),
    type channel_type NOT NULL
);

-- messages 테이블
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    content TEXT,
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE,
    author_id UUID REFERENCES users(id) ON DELETE SET NULL
);

-- message_attachments 테이블 (교차 테이블)
CREATE TABLE message_attachments (
    message_id UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    attachment_id UUID NOT NULL REFERENCES binary_contents(id) ON DELETE CASCADE,
    PRIMARY KEY (message_id, attachment_id)
);

-- read_statuses 테이블
CREATE TABLE read_statuses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE,
    last_read_at TIMESTAMPTZ NOT NULL,
    UNIQUE(user_id, channel_id)
);

-- -- 인덱스 생성 (성능 최적화)
-- CREATE INDEX idx_users_username ON users(username);
-- CREATE INDEX idx_users_email ON users(email);
-- CREATE INDEX idx_messages_channel_id ON messages(channel_id);
-- CREATE INDEX idx_messages_author_id ON messages(author_id);
-- CREATE INDEX idx_messages_created_at ON messages(created_at);
-- CREATE INDEX idx_read_statuses_user_channel ON read_statuses(user_id, channel_id);
-- CREATE INDEX idx_user_statuses_user_id ON user_statuses(user_id);

-- -- updated_at 자동 업데이트를 위한 함수
-- CREATE OR REPLACE FUNCTION update_updated_at_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = NOW();
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

-- -- updated_at 트리거 생성
-- CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_user_statuses_updated_at BEFORE UPDATE ON user_statuses
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_channels_updated_at BEFORE UPDATE ON channels
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_messages_updated_at BEFORE UPDATE ON messages
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_read_statuses_updated_at BEFORE UPDATE ON read_statuses
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
