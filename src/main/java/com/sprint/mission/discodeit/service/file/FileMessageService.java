package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final Path databasePath;


    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        try {
            //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
            this.databasePath = Paths.get(System.getProperty("user.dir"), "data", "messageDB");
            //  지정한 path에 디렉토리 없으면 생성
            if (!Files.exists(this.databasePath)) {
                try {
                    Files.createDirectories(this.databasePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message create(User msgUser, Channel channel, String text) {

        boolean isValidUser = this.channelService.getAttendees(channel).stream().anyMatch(user -> String.valueOf(user.getId()).equals(String.valueOf(msgUser.getId())));

//        this.channelService.getAttendees(channel).stream().forEach((user) -> System.out.println("attendees :" + user));
        Message msg = null;
        if (isValidUser) {
            msg = new Message(text, msgUser, channel);
//            this.data.put(msg.getId(), msg);

            // 객체를 저장할 파일 path 생성
            Path filePath = this.databasePath.resolve(String.valueOf(msg.getId()).concat(".ser"));
            // 파일 생성
            File myObj = new File(String.valueOf(filePath));

            try (
                    // 파일과 연결되는 스트림 생성
                    FileOutputStream fos = new FileOutputStream(myObj);
                    // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
            ) {

                oos.writeObject(msg);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Invalid user(" + msgUser.getName() + ") on this channel(" + channel.getName() + ")");
        }

        return msg;

    }

    @Override
    public Message read(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {

            Message message = (Message) ois.readObject();

            return message;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //FIXME : need to be readAll(UUID channelId)
    @Override
    public List<Message> readAll() {
        List<Message> messages = new ArrayList<>();

        try {

            Files.walk(this.databasePath).filter(Files::isRegularFile)
                    .forEach((path) -> {
                        try ( // 파일과 연결되는 스트림 생성
                              FileInputStream fis = new FileInputStream(String.valueOf(path));
                              // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                              ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            Message message = (Message) ois.readObject();
                            messages.add(message);
                            //FIXME
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = this.read(id);
        Message updatedMessage = this.create(message.getSender(), message.getChannel(), text);

        return updatedMessage;
    }

    @Override
    public boolean delete(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
