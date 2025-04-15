package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.util.FilePathUtil;

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
public class FileChannelRepository {
    FilePathUtil filePathUtil = new FilePathUtil();

    public boolean saveChannel(Channel channel) {
        Path path = filePathUtil.getChannelFilePath(channel.getId());

        try (
                // 위치+파일명
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel channel;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            channel = (Channel) ois.readObject();
            return channel;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> findChannelsByUserId(UUID userId) {
        Path userPath = filePathUtil.getUserFilePath(userId);
        User user;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userPath.toFile()));
            user = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<UUID> channelIds = user.getChannelIds();

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

    public List<Channel> findAllChannel() {
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

    public void updateChannelName(UUID channelId, String title) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel channel;
        if (Files.exists(path)) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                channel = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            channel.setTitle(title);

            try{
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
                oos.writeObject(channel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteChannel(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

                try{
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelPath.toFile()));
                    oos.writeObject(channel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        Path channelPath = filePathUtil.getChannelFilePath(channelId);
        Channel channel;

        if (Files.exists(channelPath)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(channelPath.toFile()))) {

                channel = (Channel) ois.readObject();
                channel.getMessages().removeIf(message -> message.getId().equals(messageId));

                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelPath.toFile()))) {
                    oos.writeObject(channel);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addUserInChannel(UUID channelId, UUID userId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel channel;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            channel = (Channel) ois.readObject();
            if (!channel.getUsersIds().contains(userId)) {
                channel.getUsersIds().add(userId);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(channel);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
