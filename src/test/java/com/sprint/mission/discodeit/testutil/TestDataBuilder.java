package com.sprint.mission.discodeit.testutil;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 테스트 데이터 생성을 위한 Builder 패턴 유틸리티
 */
public class TestDataBuilder {

  // === 고정 UUID 상수 ===
  public static final UUID USER_ID_1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
  public static final UUID USER_ID_2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
  public static final UUID CHANNEL_ID_1 = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");
  public static final UUID CHANNEL_ID_2 = UUID.fromString("223e4567-e89b-12d3-a456-426614174002");
  public static final UUID MESSAGE_ID_1 = UUID.fromString("323e4567-e89b-12d3-a456-426614174001");

  // === User 관련 ===
  public static User createDefaultUser() {
    return new User("testuser", "test@example.com", "password123", null);
  }

  public static User createUser(String username, String email) {
    return new User(username, email, "password123", null);
  }

  public static UserCreateRequest createDefaultUserCreateRequest() {
    return new UserCreateRequest("testuser", "test@example.com", "password123");
  }

  public static UserCreateRequest createUserCreateRequest(String username, String email) {
    return new UserCreateRequest(username, email, "password123");
  }

  public static UserCreateRequest createUserCreateRequest() {
    return new UserCreateRequest("testuser", "test@example.com", "password123");
  }

  public static UserCreateRequest createUserCreateRequest(String username, String email, String password) {
    return new UserCreateRequest(username, email, password);
  }

  public static UserUpdateRequest createUserUpdateRequest(String newUsername, String newEmail) {
    return new UserUpdateRequest(newUsername, newEmail, "newPassword123");
  }

  public static UserUpdateRequest createUserUpdateRequest() {
    return new UserUpdateRequest("updateduser", "updated@example.com", "newPassword123");
  }

  public static UserDto createDefaultUserDto() {
    return new UserDto(USER_ID_1, Instant.now(), Instant.now(), "testuser", "test@example.com", null, false);
  }

  public static UserDto createUserDto(UUID id, String username, String email) {
    return new UserDto(id, Instant.now(), Instant.now(), username, email, null, false);
  }

  // === Channel 관련 ===
  public static Channel createPublicChannel() {
    return new Channel(ChannelType.PUBLIC, "Test Channel", "Test Description");
  }

  public static Channel createPrivateChannel() {
    return new Channel(ChannelType.PRIVATE, null, null);
  }

  public static PublicChannelCreateRequest createPublicChannelCreateRequest() {
    return new PublicChannelCreateRequest("Test Channel", "Test Description");
  }

  public static PublicChannelCreateRequest createPublicChannelCreateRequest(String name, String description) {
    return new PublicChannelCreateRequest(name, description);
  }

  public static PrivateChannelCreateRequest createPrivateChannelCreateRequest() {
    return new PrivateChannelCreateRequest(Arrays.asList(USER_ID_1, USER_ID_2));
  }

  public static PrivateChannelCreateRequest createPrivateChannelCreateRequest(List<UUID> participantIds) {
    return new PrivateChannelCreateRequest(participantIds);
  }

  public static PublicChannelUpdateRequest createPublicChannelUpdateRequest() {
    return new PublicChannelUpdateRequest("Updated Channel", "Updated Description");
  }

  public static ChannelDto createPublicChannelDto() {
    return new ChannelDto(CHANNEL_ID_1, ChannelType.PUBLIC, "Test Channel", "Test Description", null, Instant.now());
  }

  public static ChannelDto createPrivateChannelDto() {
    return new ChannelDto(CHANNEL_ID_2, ChannelType.PRIVATE, null, null, Arrays.asList(createDefaultUserDto()),
        Instant.now());
  }

  // === Message 관련 ===
  public static Message createMessage() {
    User author = createDefaultUser();
    Channel channel = createPublicChannel();
    return new Message("Test message content", channel, author);
  }

  public static MessageCreateRequest createMessageCreateRequest() {
    return new MessageCreateRequest("Test message content", CHANNEL_ID_1, USER_ID_1);
  }

  public static MessageCreateRequest createMessageCreateRequest(String content, UUID channelId, UUID authorId) {
    return new MessageCreateRequest(content, channelId, authorId);
  }

  public static MessageUpdateRequest createMessageUpdateRequest() {
    return new MessageUpdateRequest("Updated message content");
  }

  public static MessageDto createMessageDto() {
    return new MessageDto(MESSAGE_ID_1, Instant.now(), Instant.now(), "Test message content",
        CHANNEL_ID_1, createDefaultUserDto(), null);
  }

  public static MessageDto createUpdatedMessageDto() {
    return new MessageDto(MESSAGE_ID_1, Instant.now(), Instant.now(), "Updated message content",
        CHANNEL_ID_1, createDefaultUserDto(), null);
  }

  // === BinaryContent 관련 ===
  public static BinaryContentCreateRequest createBinaryContentCreateRequest() {
    return new BinaryContentCreateRequest("test-file.jpg", "image/jpeg", new byte[1024]);
  }

  // === UserStatus 관련 ===
  public static UserStatus createUserStatus() {
    User user = createDefaultUser();
    return new UserStatus(user, Instant.now());
  }

  // === ReadStatus 관련 ===
  public static ReadStatus createReadStatus() {
    User user = createDefaultUser();
    Channel channel = createPublicChannel();
    return new ReadStatus(user, channel, Instant.now());
  }

  // === 리스트 생성 헬퍼 ===
  public static List<User> createUserList() {
    return Arrays.asList(
        createUser("user1", "user1@example.com"),
        createUser("user2", "user2@example.com"));
  }

  public static List<UserDto> createUserDtoList() {
    return Arrays.asList(
        createUserDto(USER_ID_1, "user1", "user1@example.com"),
        createUserDto(USER_ID_2, "user2", "user2@example.com"));
  }

  public static List<Message> createMessageList() {
    return Arrays.asList(createMessage(), createMessage());
  }

  public static List<MessageDto> createMessageDtoList() {
    return Arrays.asList(createMessageDto(), createMessageDto());
  }
}