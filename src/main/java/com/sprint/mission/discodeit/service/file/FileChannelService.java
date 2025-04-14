package com.sprint.mission.discodeit.service.file;

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
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileChannelService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileChannelService {
    private static final String DEFAULT_CHANNEL_NAME = "'s Channel";
    private final FilePathUtil filePathUtil = new FilePathUtil();
    private FileUserService fileUserService;
    private FileMessageService fileMessageService;

    public FileChannelService() {
    }

    public void setFileChannelService(FileMessageService fileMessageService, FileUserService fileUserService) {
        this.fileMessageService = fileMessageService;
        this.fileUserService = fileUserService;
    }

//    -------------------interface-------------------
//    UUID createChannel(UUID userId);
//    Channel findChannelById(UUID channelId);
//    List<Channel> findChannelsByUserId(UUID userId);
//    List<Channel> findAllChannel();

//    void updateChannelName(UUID channelId, String title);
//    void deleteChannel(UUID channelId);
//    void addMessageInChannel(UUID channelId, Message message);
//    void deleteMessageInChannel(UUID messageId);
//    void addUserInChannel(UUID userId, UUID channelId);
//    -------------------------------------------------

//    @Override
    public UUID createChannel(UUID userId) {

        Channel channel = new Channel(userId);
        Path path = filePathUtil.getChannelFilePath(channel.getId());
        String username;

        // 유저 유효성 검사 as early return
        if (fileUserService.findUserById(userId) == null) {
            return null;
        } else{
            username = fileUserService.findUserById(userId).getUsername();
        }

        try (
                // 위치+파일명
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            // add title
            channel.setTitle(username + DEFAULT_CHANNEL_NAME);
            // add channelId in User
            fileUserService.addChannelInUser(userId, channel.getId());
            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel.getId();
    }

//    @Override
    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);
        Channel channel;
        if (!Files.exists(path)) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            channel = (Channel) ois.readObject();
            return channel;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


//    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        Path userPath = filePathUtil.getUserFilePath(userId);
        User user;

        if (!Files.exists(userPath)) {
            return new ArrayList<>();
        }

        // 유저 객체 찾아서 가져옴
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
}
