package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFUserService.UserNotParticipantException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

  private final UserService userService;
  private final ChannelService channelService;
  private final String FILE_PATH;

  private FileMessageService(UserService userService, ChannelService channelService,
      String filePath) {
    this.userService = userService;
    this.channelService = channelService;
    FILE_PATH = filePath;
  }

  public static FileMessageService from(UserService userService, ChannelService channelService,
      String filePath) {
    return new FileMessageService(userService, channelService, filePath);
  }

  public static FileMessageService createDefault(UserService userService,
      ChannelService channelService) {
    return new FileMessageService(userService, channelService, "data/messages.ser");
  }

  @Override
  public Message createMessage(String content, UUID userId, UUID channelId) {
    List<Message> messagesRepository = loadData();
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Channel channel = channelService.getChannelById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    boolean isNotParticipant = channel.getParticipants().stream()
        .noneMatch(p -> p.getId().equals(userId));
    if (isNotParticipant) {
      throw new UserNotParticipantException();
    }

    Message message = Message.create(content, userId, channelId);
    messagesRepository.add(message);
    saveData(messagesRepository);
    return message;
  }

  @Override
  public Optional<Message> getMessageById(UUID id) {
    List<Message> messagesRepository = loadData();
    return findMessageById(messagesRepository, id);
  }

  @Override
  public List<Message> searchMessages(UUID channelId, UUID userId, String content) {
    List<Message> messagesRepository = loadData();
    return messagesRepository.stream()
        .filter(message -> !message.isDeleted())
        .filter(message -> (channelId == null || message.getChannelId().equals(channelId)) &&
            (userId == null || message.getUserId().equals(userId)) &&
            (content == null || message.getContent().contains(content)))
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Message> getChannelMessages(UUID channelId) {
    List<Message> messagesRepository = loadData();
    return messagesRepository.stream()
        .filter(m -> !m.isDeleted())
        .filter(m -> m.getChannelId().equals(channelId))
        .sorted(Comparator.comparingLong(Message::getCreatedAt))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Message> updateMessageContent(UUID id, String content) {
    List<Message> messagesRepository = loadData();
    Optional<Message> messageOpt = findMessageById(messagesRepository, id).map(message -> {
      message.updateContent(content);
      return message;
    });

    if (messageOpt.isPresent()) {
      saveData(messagesRepository);
    }
    return messageOpt;
  }

  @Override
  public Optional<Message> deleteMessage(UUID id) {
    List<Message> messagesRepository = loadData();
    Optional<Message> messageOpt = findMessageById(messagesRepository, id).filter(
        message -> !message.isDeleted()).map(message -> {
      message.delete();
      return message;
    });

    if (messageOpt.isPresent()) {
      saveData(messagesRepository);
    }
    return messageOpt;
  }

  private Optional<Message> findMessageById(List<Message> messagesRepository, UUID id) {
    return messagesRepository.stream().filter(m -> m.getId().equals(id)).findFirst();
  }

  @SuppressWarnings("unchecked")
  private List<Message> loadData() {
    List<Message> messagesRepository = new ArrayList<>();
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      createData();
      return messagesRepository;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Object obj = ois.readObject();
      if (obj instanceof List) {
        messagesRepository.addAll((List<Message>) obj);
      }
    } catch (FileNotFoundException e) {
      // 파일이 없을 경우 초기 상태 유지
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return messagesRepository;
  }

  private void createData() {
    File file = new File(FILE_PATH);
    File parentDir = file.getParentFile();

    if (parentDir != null && !parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        System.err.println("폴더 생성 실패: " + parentDir.getAbsolutePath());
        return;
      }
    }

    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveData(List<Message> messagesRepository) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(messagesRepository);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(UUID messageId) {
      super("메시지를 찾을 수 없음: " + messageId);
    }
  }
}