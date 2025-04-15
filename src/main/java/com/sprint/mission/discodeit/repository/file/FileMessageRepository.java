package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileMessageRepository implements MessageRepository {
    private static final String MESSAGE_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/Message.txt";

    @Override
    public void save(Message message) {
        File file = new File(MESSAGE_FILE_REPOSITORY_PATH);
        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화( 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
