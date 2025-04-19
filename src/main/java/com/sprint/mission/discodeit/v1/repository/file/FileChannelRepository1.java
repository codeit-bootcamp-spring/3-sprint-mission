package com.sprint.mission.discodeit.v1.repository.file;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.ChannelRepository1;
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
public class FileChannelRepository1 implements ChannelRepository1 {
    FilePathUtil filePathUtil;
    FileSerializer fileSerializer;

    public FileChannelRepository1(FilePathUtil filePathUtil, FileSerializer fileSerializer) {
        this.filePathUtil = filePathUtil;
        this.fileSerializer = fileSerializer;
    }

    @Override
    public void saveChannel(Channel1 channel) {
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
    public Channel1 findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);

        if (!Files.exists(path)) {
            return null;
        }
        return fileSerializer.readFile(path, Channel1.class);
    }
    @Override
    public List<Channel1> findChannelsByChannelIds(List<UUID> channelIds) {

        // 채널 조회 리스트 생성
        Path channelDirectory = filePathUtil.getChannelDirectory();
        if (Files.exists(channelDirectory)) {
            try {
                List<Channel1> list = Files.list(channelDirectory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Channel1) data;
                            } catch (IOException | ClassNotFoundException exception) {
                                throw new RuntimeException(exception);
                            }
                        }).toList();
                List<Channel1> result = new ArrayList<>();
                for (Channel1 channel : list) {
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
    public List<Channel1> findAllChannels() {
        Path directory = filePathUtil.getChannelDirectory();

        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }

        try {
            List<Channel1> list = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Channel1) data;
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
        Channel1 channel;
        if (Files.exists(path)) {
            channel = fileSerializer.readFile(path, Channel1.class);
            channel.setTitle(title);
            fileSerializer.writeFile(path, channel);
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
    public void addMessageInChannel(UUID channelId, Message1 message) {
        Path channelPath = filePathUtil.getChannelFilePath(channelId);
        Path messagePath = filePathUtil.getMessageFilePath(message.getId());
        Channel1 channel;

        if (Files.exists(messagePath)) {
            if (Files.exists(channelPath)) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(channelPath.toFile()));
                    channel = (Channel1) ois.readObject();
                    if (!channel.getMessages().contains(message)) {
                        channel.getMessages().add(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                fileSerializer.writeFile(channelPath, channel);

            }
        }
    }
    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        Path channelPath = filePathUtil.getChannelFilePath(channelId);

        if (Files.exists(channelPath)) {
            Channel1 channel = fileSerializer.readFile(channelPath, Channel1.class);
            channel.getMessages().removeIf(message -> message.getId().equals(messageId));
            fileSerializer.writeFile(channelPath, channel);
        }
    }
    @Override
    public void addUserInChannel(UUID channelId, UUID userId) {
        Path path = filePathUtil.getChannelFilePath(channelId);

        Channel1 channel = fileSerializer.readFile(path, Channel1.class);
        if (channel.getUsersIds().contains(userId)) {
            channel.getUsersIds().add(userId);
        }
        fileSerializer.writeFile(path, channel);

    }
}
