package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.dto.entity.Message;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository("fileMessageRepository")
public class FileMessageRepository implements MessageRepository {
    private static final String DIR = "data/messages/";

    public FileMessageRepository() {
        clearFile();
    }

    @Override
    public void save(Message msg) {
        try (
                FileOutputStream fos = new FileOutputStream(DIR + msg.getId() + ".ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message loadById(UUID id) {
        File file = new File(DIR + id + ".ser");
        if (!file.exists()) {
            throw new IllegalArgumentException("[Message] 유효하지 않은 message 파일 (" + id + ".ser)");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Message] 메시지 로드 중 오류 발생", e);
        }
    }

    @Override
    public List<Message> loadAll() {
        if (Files.exists(Path.of(DIR))) {
            try {
                List<Message> msgs = Files.list(Paths.get(DIR))
                        .map( path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Message) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return msgs;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Message> loadByChannel(UUID channelId) {
        List<Message> messages = loadAll();

        messages = messages.stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();

        return messages;
    }

    @Override
    public void update(UUID id, String content) {
        Message msg = loadById(id);
        if (msg == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 메시지입니다. (" + id + ")");
        }

        msg.update(content);
        save(msg);
    }

    @Override
    public void deleteById(UUID id) {
        File file = new File(DIR + id + ".ser");

        try {
            if (file.exists()) {
                if (!file.delete()) { //file.delete는 실패했을 때 예외 반환 x
                    System.out.println("[Message] 파일 삭제 실패");
                };
            }
            else {
                System.out.println("[Message] 유효하지 않은 파일 (" + id + ")");
            }
        } catch (Exception e) {
            throw new RuntimeException("[Message] 파일 삭제 중 오류 발생 (" + id + ")", e);
        }
    }

    private void clearFile() {
        File dir = new File(DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) { return; }

            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    throw new RuntimeException("[Message] messages 폴더 초기화 실패", e);
                }
            }
        }
    }
}
