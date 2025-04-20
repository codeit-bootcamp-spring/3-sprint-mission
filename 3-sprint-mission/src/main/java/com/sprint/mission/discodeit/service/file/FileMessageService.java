package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final Path directory = Paths.get(System.getProperty("user.dir"),"data");
    private static final Path filepath = Paths.get(String.valueOf(directory), "messages.ser");
    private List<Message> data;

    public FileMessageService() throws IOException {
        init(directory);
        this.data = load(filepath);
    }

    // 저장할 경로의 파일 초기화
    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void save(T data) {
        try(
                FileOutputStream fos = new FileOutputStream(filepath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Message> load(Path filepath) {
        if (!Files.exists(filepath)) {
            return new ArrayList<>();
        }

        try (
                FileInputStream fis = new FileInputStream(filepath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (List<Message>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public Message create(User currentUser, Channel currentChannel, String text) {
        Message message = null;

        if (currentUser.getIsLogin() && currentChannel != null) {
            message = new Message(currentUser.getId(), currentChannel.getId(), text);
            this.data.add(message);

            save(this.data);

        } else if (!currentUser.getIsLogin()) {
            System.out.println("먼저 로그인하십시오.");

        } else {
            System.out.println("잘못된 접근입니다.");

        }

        return message;
    }

    @Override
    public List<Message> readAll(User currentUser, Channel currentChannel) {
        List<Message> messages = null;

        if (currentUser.getIsLogin() && currentChannel != null) {
            messages = this.data.stream()
                    .filter(m -> m.getChannel().equals(currentChannel.getId()))
                    .collect(Collectors.toList());

        } else if (!currentUser.getIsLogin()) {
            System.out.println("먼저 로그인하십시오.");

        } else if (currentChannel == null) {

            System.out.println("잘못된 접근입니다.");
        }

        return messages;
    }

    @Override
    public Message find(User currentUser, Channel currentChannel, UUID id) {

        return readAll(currentUser, currentChannel).stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지는 존재하지 않습니다."));
    };

    @Override
    public List<Message> findByText(User currentUser, Channel currentChannel, String text) {

        return readAll(currentUser, currentChannel).stream()
                .filter(m -> m.getText().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void update(User currentUser, Channel currentChannel, UUID id, String text) {
        Message message = find(currentUser, currentChannel, id);

        if (message.getSender().equals(currentUser.getId())) {
            message.updateText(id, text);

            save(this.data);

        } else {
            System.out.println("권한이 없습니다.");
        }
    }

    @Override
    public void delete(User currentUser, Channel currentChannel, UUID id) {
        Message message = find(currentUser, currentChannel, id);

        if (message.getSender().equals(currentChannel.getId())) {
            this.data.removeIf(m -> m.getId().equals(id));

            save(this.data);

        } else {
            System.out.println("권한이 없습니다.");
        }
    }

}
