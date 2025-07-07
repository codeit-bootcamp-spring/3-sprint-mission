create schema if not exists discodeit;

CREATE TABLE discodeit.tbl_user (
    id uuid primary key,
    username varchar(50) unique not null,
    email varchar(100) unique not null,
    password varchar(60) not null,
    profile_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE discodeit.tbl_user_status (
    id uuid primary key,
    last_active_at timestamp with time zone NOT NULL,
    user_id uuid unique,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE discodeit.tbl_channel (
    id uuid primary key,
    name varchar(100),
    description varchar(500),
    type varchar(20) not null,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE discodeit.tbl_message (
    id uuid primary key,
    content text,
    channel_id uuid not null,
    author_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE discodeit.tbl_message_attachment (
    id serial primary key,
    message_id uuid,
    attachment_id uuid
);

CREATE TABLE discodeit.tbl_read_status (
    id uuid primary key,
    last_read_at timestamp with time zone not null,
    user_id uuid,
    channel_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE discodeit.tbl_binary_content (
    id uuid primary key,
    file_name varchar(255) not null,
    size bigint not null,
    content_type VARCHAR(255) not null,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

ALTER TABLE discodeit.tbl_user ADD CONSTRAINT fk_user_profile FOREIGN KEY (profile_id) REFERENCES discodeit.tbl_binary_content(id) ON DELETE SET NULL;
ALTER TABLE discodeit.tbl_user_status ADD FOREIGN KEY (user_id) REFERENCES discodeit.tbl_user(id) ON DELETE CASCADE;
ALTER TABLE discodeit.tbl_read_status ADD FOREIGN KEY (user_id) REFERENCES discodeit.tbl_user(id) ON DELETE CASCADE;
ALTER TABLE discodeit.tbl_read_status ADD FOREIGN KEY (channel_id) REFERENCES discodeit.tbl_channel(id) ON DELETE CASCADE;
ALTER TABLE discodeit.tbl_read_status ADD UNIQUE (user_id, channel_id);
ALTER TABLE discodeit.tbl_message ADD FOREIGN KEY (author_id) REFERENCES discodeit.tbl_user(id) ON DELETE SET NULL;
ALTER TABLE discodeit.tbl_message ADD FOREIGN KEY (channel_id) REFERENCES discodeit.tbl_channel(id) ON DELETE CASCADE;
ALTER TABLE discodeit.tbl_message_attachment ADD FOREIGN KEY (message_id) REFERENCES discodeit.tbl_message(id) ON DELETE CASCADE;
ALTER TABLE discodeit.tbl_message_attachment ADD FOREIGN KEY (attachment_id) REFERENCES discodeit.tbl_binary_content(id) ON DELETE CASCADE;

-- -- 바이너리 콘텐츠
-- INSERT INTO tbl_binary_content (id, file_name, size, content_type, created_at)
-- VALUES ('b1f83f77-1b1d-4c56-ae99-0b1ccf2d2024', 'profile.jpg', 123, 'jpg', '2025-06-19T08:00:00Z');
--
-- -- 사용자
-- INSERT INTO tbl_user (id, username, email, password, profile_id, created_at, updated_at)
-- VALUES ('11111111-1111-1111-1111-111111111111', 'tester', 'tester@example.com', 'pw1234', 'b1f83f77-1b1d-4c56-ae99-0b1ccf2d2024', '2025-06-19T08:00:01Z', '2025-06-19T08:00:01Z');
--
-- -- 사용자 상태
-- INSERT INTO tbl_user_status (id, user_id, last_active_at, created_at, updated_at)
-- VALUES ('22222222-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:02Z', '2025-06-19T08:00:02Z', '2025-06-19T08:00:02Z');
--
-- -- 채널
-- INSERT INTO tbl_channel (id, type, name, description, created_at, updated_at)
-- VALUES ('33333333-1111-1111-1111-111111111111', 'PUBLIC', 'general', '일반 채널입니다.', '2025-06-19T08:00:03Z', '2025-06-19T08:00:03Z');
--
-- -- 메시지 5개 (2초 간격)
-- INSERT INTO tbl_message (id, content, channel_id, author_id, created_at, updated_at)
-- VALUES
--     ('aaaaaaaa-aaaa-bbbb-cccc-000000000001', '내용 0', '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:05Z', '2025-06-19T08:00:05Z'),
--     ('aaaaaaaa-aaaa-bbbb-cccc-000000000002', '내용 1', '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:07Z', '2025-06-19T08:00:07Z'),
--     ('aaaaaaaa-aaaa-bbbb-cccc-000000000003', '내용 2', '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:09Z', '2025-06-19T08:00:09Z'),
--     ('aaaaaaaa-aaaa-bbbb-cccc-000000000004', '내용 3', '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:11Z', '2025-06-19T08:00:11Z'),
--     ('aaaaaaaa-aaaa-bbbb-cccc-000000000005', '내용 4', '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '2025-06-19T08:00:13Z', '2025-06-19T08:00:13Z');
