package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileChannelRepository implements ChannelRepository {
    FilePathUtil filePathUtil = new FilePathUtil();
    FileSerializer fileSerializer = new FileSerializer();


    @Override
    public Channel createChannelByName(String name) {
        Channel channel = new Channel(name);
        Path path = filePathUtil.getChannelFilePath(channel.getId());
        fileSerializer.writeFile(path,channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        return fileSerializer.readFile(path, Channel.class);

    }

    @Override
    public List<Channel> findAllChannel() {
        try {
            Path directory = filePathUtil.getChannelDirectory();

            if (!Files.exists(directory)) {
                return new ArrayList<>();
            }

            try {
                List<Channel> list = Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Channel) data;
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
        Channel channel = fileSerializer.readFile(path, Channel.class);
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
