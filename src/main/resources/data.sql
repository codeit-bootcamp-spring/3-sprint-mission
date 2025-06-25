-- 기존 데이터 모두 삭제
DELETE
FROM message_attachments;
DELETE
FROM messages;
DELETE
FROM read_statuses;
DELETE
FROM user_statuses;
DELETE
FROM channels;
DELETE
FROM users;
DELETE
FROM binary_contents;

-- BinaryContent 초기 데이터
INSERT INTO binary_contents (id, created_at, file_name, size, content_type)
VALUES ('00000000-0000-0000-0000-000000000001', now(), 'default-profile.png', 1024, 'image/png'),
       ('00000000-0000-0000-0000-000000000002', now(), 'message-attachment.png', 2048, 'image/png');

-- User 초기 데이터 (2명)
INSERT INTO users (id, created_at, username, email, password, profile_id)
VALUES ('00000000-0000-0000-0000-000000000010', now(), 'admin', 'admin@example.com',
        'hashedpassword1', '00000000-0000-0000-0000-000000000001'),
       ('00000000-0000-0000-0000-000000000011', now(), 'user2', 'user2@example.com',
        'hashedpassword2', null);

-- UserStatus 초기 데이터
INSERT INTO user_statuses (id, created_at, user_id, last_active_at)
VALUES ('00000000-0000-0000-0000-000000000020', now(), '00000000-0000-0000-0000-000000000010',
        now()),
       ('00000000-0000-0000-0000-000000000021', now(), '00000000-0000-0000-0000-000000000011',
        now());

-- Channel 초기 데이터 (public, private)
INSERT INTO channels (id, created_at, type, name, description)
VALUES ('00000000-0000-0000-0000-000000000030', now(), 'public', 'general',
        'Public general discussion channel'),
       ('00000000-0000-0000-0000-000000000031', now(), 'private', 'team',
        'Private team collaboration channel');

-- Message 초기 데이터
INSERT INTO messages (id, created_at, content, channel_id, author_id)
VALUES ('00000000-0000-0000-0000-000000000040', now(), 'Welcome to the general channel!',
        '00000000-0000-0000-0000-000000000030', '00000000-0000-0000-0000-000000000010'),
       ('00000000-0000-0000-0000-000000000041', now(), 'Hello team!',
        '00000000-0000-0000-0000-000000000031', '00000000-0000-0000-0000-000000000011');

-- MessageAttachment 초기 데이터
INSERT INTO message_attachments (message_id, attachment_id)
VALUES ('00000000-0000-0000-0000-000000000040', '00000000-0000-0000-0000-000000000002');

-- ReadStatus 초기 데이터
INSERT INTO read_statuses (id, created_at, user_id, channel_id, last_read_at)
VALUES ('00000000-0000-0000-0000-000000000050', now(), '00000000-0000-0000-0000-000000000010',
        '00000000-0000-0000-0000-000000000030', now()),
       ('00000000-0000-0000-0000-000000000051', now(), '00000000-0000-0000-0000-000000000011',
        '00000000-0000-0000-0000-000000000031', now());
