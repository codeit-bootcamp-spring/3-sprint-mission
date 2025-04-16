package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileChannelRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class FileChannelRepository implements ChannelRepository {
    FilePathUtil filePathUtil;
    FileSerializer fileSerializer;

    public FileChannelRepository(FilePathUtil filePathUtil, FileSerializer fileSerializer) {
        this.filePathUtil = filePathUtil;
        this.fileSerializer = fileSerializer;
    }

    @Override
    public void saveChannel(Channel channel) {
        Path path = filePathUtil.getChannelFilePath(channel.getId());

        try (
                // 위치+파일명
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        return fileSerializer.readObject(path, Channel.class);
    }
    @Override
    public List<Channel> findChannelsByChannelIds(List<UUID> channelIds) {
        // 채널 조회 리스트 생성
        Path channelDirectory = filePathUtil.getChannelDirectory();
        if (Files.exists(channelDirectory)) {
            try {
                List<Channel> list = Files.list(channelDirectory)
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
                List<Channel> result = new ArrayList<>();
                for (Channel channel : list) {
                    if (channelIds.contains(channel.getId())) {
                        result.add(channel);
                    }
                }
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    public List<Channel> findAllChannels() {
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
    }
    @Override
    public void updateChannelNameById(UUID channelId, String title) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel channel;
        if (Files.exists(path)) {
            channel = fileSerializer.readObject(path, Channel.class);
            channel.setTitle(title);
            fileSerializer.writeObject(path, channel);
        }
    }
    @Override
    public void deleteChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void addMessageInChannel(UUID channelId, Message message) {
        Path channelPath = filePathUtil.getChannelFilePath(channelId);
        Path messagePath = filePathUtil.getMessageFilePath(message.getId());
        Channel channel;

        if (Files.exists(messagePath)) {
            if (Files.exists(channelPath)) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(channelPath.toFile()));
                    channel = (Channel) ois.readObject();
                    if (!channel.getMessages().contains(message)) {
                        channel.getMessages().add(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                fileSerializer.writeObject(channelPath, channel);

            }
        }
    }
    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        Path channelPath = filePathUtil.getChannelFilePath(channelId);

        if (Files.exists(channelPath)) {
            Channel channel = fileSerializer.readObject(channelPath, Channel.class);
            channel.getMessages().removeIf(message -> message.getId().equals(messageId));
            fileSerializer.writeObject(channelPath, channel);
        }
    }
    @Override
    public void addUserInChannel(UUID channelId, UUID userId) {
        Path path = filePathUtil.getChannelFilePath(channelId);

        Channel channel = fileSerializer.readObject(path, Channel.class);
        if (channel.getUsersIds().contains(userId)) {
            channel.getUsersIds().add(userId);
        }
        fileSerializer.writeObject(path, channel);

    }
}
