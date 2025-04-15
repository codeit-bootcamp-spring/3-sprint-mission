package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class FileChannelRepository implements ChannelRepository {
    private static final String CHANNEL_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/channel.txt";

    @Override
    public void save(Channel channel) {
        File file = new File(CHANNEL_FILE_REPOSITORY_PATH);
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
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
