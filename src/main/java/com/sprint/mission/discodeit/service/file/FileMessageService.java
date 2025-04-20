package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileMessageService implements MessageService {
  private static final String FILE_PATH = "messages.ser";

  private Map<UUID, Message> messageMap = new HashMap<>();
  private Map<UUID, List<Message>> channelMessagesMap = new HashMap<>();
  private Map<UUID, List<Message>> userMessagesMap = new HashMap<>();

  private final UserService userService;
  private final ChannelService channelService;
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM월 dd일 a hh시 mm분 ss초");

  public FileMessageService(UserService userService, ChannelService channelService) {
    this.userService = userService;
    this.channelService = channelService;
    loadData();
  }

  private void loadData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) return;

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      messageMap = (Map<UUID, Message>) ois.readObject();
      channelMessagesMap = (Map<UUID, List<Message>>) ois.readObject();
      userMessagesMap = (Map<UUID, List<Message>>) ois.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(messageMap);
      oos.writeObject(channelMessagesMap);
      oos.writeObject(userMessagesMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Message createMessage(UUID channelId, UUID senderId, String content) {
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    User sender = userService.getUserById(senderId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다. senderId: " + senderId));

    if (channel.getChannelUsers().size() < 2) {
      throw new IllegalStateException("채널에 최소 두 명 이상의 유저가 있어야 메시지를 보낼 수 있습니다.");
    }
    if (!channel.getChannelUsers().contains(sender)) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    Message message = new Message(content, channelId, senderId);
    messageMap.put(message.getId(), message);
    channelMessagesMap.computeIfAbsent(channelId, k -> new ArrayList<>()).add(message);
    userMessagesMap.computeIfAbsent(senderId, k -> new ArrayList<>()).add(message);

    saveData();
    return message;
  }

  @Override
  public List<Message> getMessagesBySenderInChannel(UUID channelId, UUID senderId) {
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    if (channel.getChannelUsers().stream().noneMatch(u -> u.getId().equals(senderId))) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    return channelMessagesMap.getOrDefault(channelId, Collections.emptyList())
        .stream()
        .filter(m -> m.getSenderId().equals(senderId))
        .toList();
  }

  @Override
  public List<Message> getMessagesByReceiverInChannel(UUID channelId, UUID receiverId) {
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    if (channel.getChannelUsers().stream().noneMatch(u -> u.getId().equals(receiverId))) {
      throw new IllegalArgumentException("해당 유저는 이 채널의 멤버가 아닙니다.");
    }

    return channelMessagesMap.getOrDefault(channelId, Collections.emptyList())
        .stream()
        .filter(m -> !m.getSenderId().equals(receiverId))
        .toList();
  }

  @Override
  public List<Message> getAllSentMessages(UUID senderId) {
    return userMessagesMap.getOrDefault(senderId, Collections.emptyList());
  }

  @Override
  public List<Message> getAllReceivedMessages(UUID receiverId) {
    List<UUID> myChannels = channelService.getAllChannels().stream()
        .filter(ch -> ch.getChannelUsers().stream().anyMatch(u -> u.getId().equals(receiverId)))
        .map(Channel::getId)
        .toList();

    List<Message> received = new ArrayList<>();
    for (UUID chId : myChannels) {
      List<Message> messages = channelMessagesMap.getOrDefault(chId, Collections.emptyList());
      for (Message msg : messages) {
        if (!msg.getSenderId().equals(receiverId)) {
          received.add(msg);
        }
      }
    }
    return received;
  }

  @Override
  public void updateMessage(UUID messageId, UUID senderId, String newContent) {
    Message msg = messageMap.get(messageId);
    if (msg == null) throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
    if (!msg.getSenderId().equals(senderId)) {
      throw new SecurityException("해당 메시지를 수정할 권한이 없습니다.");
    }
    msg.updateContent(newContent);
    saveData();
  }

  @Override
  public void deleteMessage(UUID messageId, UUID senderId) {
    Message msg = messageMap.get(messageId);
    if (msg == null) throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
    if (!msg.getSenderId().equals(senderId)) {
      throw new SecurityException("해당 메시지를 삭제할 권한이 없습니다.");
    }
    messageMap.remove(messageId);
    channelMessagesMap.getOrDefault(msg.getChannelId(), new ArrayList<>()).remove(msg);
    userMessagesMap.getOrDefault(senderId, new ArrayList<>()).remove(msg);
    saveData();
  }

  @Override
  public List<Message> getAllMessagesInChannel(UUID channelId, UUID requesterId) {
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    if (channel.getChannelUsers().stream().noneMatch(u -> u.getId().equals(requesterId))) {
      throw new SecurityException("채널에 접근할 수 있는 권한이 없습니다.");
    }

    return channelMessagesMap.getOrDefault(channelId, Collections.emptyList());
  }

  public Optional<Message> getMessageById(UUID messageId) {
    return Optional.ofNullable(messageMap.get(messageId));
  }

  public String formatMessage(Message message) {
    boolean isEdited = !message.getCreatedAt().equals(message.getUpdatedAt());
    long timestamp = isEdited ? message.getUpdatedAt() : message.getCreatedAt();
    String formattedDate = DATE_FORMAT.format(new Date(timestamp));

    Optional<User> senderOpt = userService.getUserById(message.getSenderId());
    String senderName = senderOpt.map(User::getUsername).orElse("알 수 없음");
    String senderEmail = senderOpt.map(User::getEmail).orElse("이메일 없음");

    String channelName = channelService.getChannelById(message.getChannelId())
        .map(Channel::getChannelName)
        .orElse("알 수 없음");

    return String.format("[%s] %s, 보낸사람: %s(%s), 내용: \"%s\"",
        channelName, formattedDate, senderName, senderEmail, message.getContent());
  }
}
