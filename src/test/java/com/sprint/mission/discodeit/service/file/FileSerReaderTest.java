package com.sprint.mission.discodeit.service.file;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class FileSerReaderTest {

  private class FileSerReader {

    public static Object readObjectFromFile(String filePath)
        throws IOException, ClassNotFoundException {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
        return ois.readObject();
      }
    }
  }

  @Test
  public void testReadUsersFromFile() throws IOException, ClassNotFoundException {
    HashMap<UUID, User> userData = (HashMap<UUID, User>) FileSerReader.readObjectFromFile(
        "data/users.ser"); // 파일 경로 수정
    if (userData != null) {
      List<User> users = userData.values().stream().collect(Collectors.toList());
      users.forEach(System.out::println);
    } else {
      System.out.println("파일이 비어있거나 읽을 수 없습니다.");
    }
  }

  @Test
  public void testReadChannelsFromFile() throws IOException, ClassNotFoundException {
    HashMap<UUID, Channel> channelData = (HashMap<UUID, Channel>) FileSerReader.readObjectFromFile(
        "data/channels.ser"); // 파일 경로 수정
    if (channelData != null) {
      List<Channel> channels = channelData.values().stream().collect(Collectors.toList());
      channels.forEach(System.out::println);
    } else {
      System.out.println("파일이 비어있거나 읽을 수 없습니다.");
    }
  }

  @Test
  public void testReadMessagesFromFile() throws IOException, ClassNotFoundException {
    String filePath = "data/messages.ser";
    File file = new File(filePath);

    if (file.exists() && file.length() > 0) { // 파일이 존재하고 비어있지 않은 경우
      HashMap<UUID, Message> messagesData = (HashMap<UUID, Message>) FileSerReader.readObjectFromFile(
          filePath);

      assertNotNull(messagesData, "파일이 비어있거나 읽을 수 없습니다.");
      assertFalse(messagesData.isEmpty(), "저장된 메시지가 없습니다.");
      List<Message> messages = messagesData.values().stream().collect(Collectors.toList());
      messages.forEach(System.out::println);
    } else {
      System.out.println("파일이 존재하지 않거나 비어있습니다: " + filePath);
    }
  }
}
