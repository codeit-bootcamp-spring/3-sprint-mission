package com.sprint.mission.discodeit.refactor.repository.file;

import com.sprint.mission.discodeit.refactor.entity.Channel2;
import com.sprint.mission.discodeit.refactor.repository.ChannelRepository2;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.file
 * fileName       : FileChannelRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class FileChannelRepository2 implements ChannelRepository2 {
    FilePathUtil filePathUtil;
    FileSerializer fileSerializer;

    public FileChannelRepository2(FilePathUtil filePathUtil, FileSerializer fileSerializer) {
        this.filePathUtil = filePathUtil;
        this.fileSerializer = fileSerializer;
    }

    @Override
    public Channel2 createChannelByName(String name) {
        Channel2 channel = new Channel2(name);
        Path path = filePathUtil.getChannelFilePath(channel.getId());
        fileSerializer.writeFile(path,channel);
        return channel;
    }

    @Override
    public Channel2 findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        return fileSerializer.readFile(path, Channel2.class);

    }

    @Override
    public List<Channel2> findAllChannel() {
        try {
            Path directory = filePathUtil.getChannelDirectory();

            if (!Files.exists(directory)) {
                return new ArrayList<>();
            }

            try {
                List<Channel2> list = Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Channel2) data;
                            } catch (IOException | ClassNotFoundException exception) {
                                throw new RuntimeException(exception);
                            }
                        }).toList();
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateChannel(UUID channelId, String name) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel2 channel = fileSerializer.readFile(path, Channel2.class);
        channel.setName(name);
        fileSerializer.writeFile(path, channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
