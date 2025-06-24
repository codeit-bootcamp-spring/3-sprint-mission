create schema if not exists discodeit;
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TABLE tbl_user (
    id uuid primary key,
    username varchar(50) unique not null,
    email varchar(100) unique not null,
    password varchar(60) not null,
    profile_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE tbl_user_status (
    id uuid primary key,
    last_active_at timestamp with time zone NOT NULL,
    user_id uuid unique,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE tbl_channel (
    id uuid primary key,
    name varchar(100),
    description varchar(500),
    type channel_type not null,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE tbl_message (
    id uuid primary key,
    content text,
    channel_id uuid not null,
    author_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE tbl_message_attachment (
    id serial primary key,
    message_id uuid,
    attachment_id uuid
);

CREATE TABLE tbl_read_status (
    id uuid primary key,
    last_read_at timestamptz not null,
    user_id uuid,
    channel_id uuid,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

CREATE TABLE tbl_binary_content (
    id uuid primary key,
    file_name varchar(255) not null,
    size bigint not null,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone
);

ALTER TABLE tbl_user ADD CONSTRAINT fk_user_profile FOREIGN KEY (profile_id) REFERENCES tbl_binary_content(id) ON DELETE SET NULL;
ALTER TABLE tbl_user_status ADD FOREIGN KEY (user_id) REFERENCES tbl_user(id) ON DELETE CASCADE;
ALTER TABLE tbl_read_status ADD FOREIGN KEY (user_id) REFERENCES tbl_user(id) ON DELETE CASCADE;
ALTER TABLE tbl_read_status ADD FOREIGN KEY (channel_id) REFERENCES tbl_channel(id) ON DELETE CASCADE;
ALTER TABLE tbl_read_status ADD UNIQUE (user_id, channel_id);
ALTER TABLE tbl_message ADD FOREIGN KEY (author_id) REFERENCES tbl_user(id) ON DELETE SET NULL;
ALTER TABLE tbl_message ADD FOREIGN KEY (channel_id) REFERENCES tbl_channel(id) ON DELETE CASCADE;
ALTER TABLE tbl_message_attachment ADD FOREIGN KEY (message_id) REFERENCES tbl_message(id) ON DELETE CASCADE;
ALTER TABLE tbl_message_attachment ADD FOREIGN KEY (attachment_id) REFERENCES tbl_binary_content(id) ON DELETE CASCADE;
