package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final String fileName = "src/main/java/com/sprint/mission/discodeit/service/file/messages.ser";
    private final File file = new File(fileName);

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        // 존재하지 않는 사용자를 sender로 설정했을 때 예외 처리
        if (userService.findById(message.getSenderId()).isEmpty()) {
            throw new NotFoundUserException();
        }

        // 존재하지 않는 채널에 메시지를 보낼 때 예외 처리
        if (channelService.findById(message.getChannelId()).isEmpty()) {
            throw new NotFoundChannelException();
        }

        Channel foundChannel = channelService.findById(message.getChannelId()).get();

        // 메시지를 보낸 user의 mesagesList에 해당 메시지 추가
        userService.findById(message.getSenderId()).ifPresent(user -> {
            // 해당 채널에 속해 있지 않는 User가 메시지를 보낼 때 예외 처리
            if (!user.getChannels().contains(foundChannel)) {
                throw new UserNotInChannelException();
            } else {
                user.getMessages().add(message);
                userService.update(user);
            }
        });

        // 메시지를 보낸 channel의 mesagesList에 해당 메시지 추가
        channelService.findById(message.getChannelId()).ifPresent(channel -> {
            channel.getMessageList().add(message);
            channelService.update(channel);
        });

        // Message 저장
        data.put(message.getId(), message);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        // 조건에 맞는 Message
        Optional<Message> foundMessage = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(messageId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundMessage;
    }

    @Override
    public List<Message> findAll() {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        return data.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        return data.values().stream()
                .filter(message -> message.getContent().contains(word))
                .toList();
    }

    @Override
    public Message update(Message message) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        data.put(message.getId(), message);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 변경된 메시지를 메시지를 보낸 User의 messageList에 반영
        userService.findAll().forEach(user -> {
            List<Message> messages = user.getMessages();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).equals(message)) {
                    messages.set(i, message);
                }
            }
            userService.update(user);
        });

        // 변경된 메시지를 메시지가 있는 Channel의 messageList에 반영
        channelService.findAll().forEach(channel -> {
            List<Message> messages = channel.getMessageList();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).equals(message)) {
                    messages.set(i, message);
                }
            }
            channelService.update(channel);
        });

        return message;
    }

    @Override
    public void deleteById(UUID messageId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = readFile();

        data.remove(messageId);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 메시지를 보낸 User의 messageList에서 해당 메시지 삭제
        userService.findAll().forEach(user -> {
            List<Message> messages = user.getMessages();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).getId().equals(messageId)) {
                    messages.remove(messages.get(i));
                }
            }
            userService.update(user);
        });

        // 메시지가 있는 Channel의 messageList에서 해당 메시지 삭제
        channelService.findAll().forEach(channel -> {
            List<Message> messages = channel.getMessageList();
            for (int i=0; i<messages.size(); i++) {
                if (messages.get(i).getId().equals(messageId)) {
                    messages.remove(messages.get(i));
                }
            }
            channelService.update(channel);
        });
    }

    private Map<UUID, Message> readFile() {
        Map<UUID, Message> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Message>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
