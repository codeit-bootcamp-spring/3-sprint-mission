package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FilePathUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
public class FileChannelService implements ChannelService {
    private static final String DEFAULT_CHANNEL_NAME = "'s Channel";
    private final FilePathUtil filePathUtil = new FilePathUtil();
    private final FileChannelRepository fcr = new FileChannelRepository();
    private UserService userService;
    private MessageService messageService;

    public void setService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

//    -------------------interface-------------------
//    UUID createChannel(UUID userId);
//    Channel findChannelById(UUID channelId);
//    List<Channel> findChannelsByUserId(UUID userId);
//    List<Channel> findAllChannel();
//    void updateChannelName(UUID channelId, String title);
//    void deleteChannel(UUID channelId);
//    void addMessageInChannel(UUID channelId, Message message); - NOT TESTED
//    void deleteMessageInChannel(UUID messageId); - NOT TESTED
//    void addUserInChannel(UUID userId, UUID channelId);
//    -------------------------------------------------

    @Override
    public UUID createChannel(UUID userId) {

        Channel channel = new Channel(userId);


        // 유저 유효성 검사 as early return
        if (userService.findUserById(userId) == null) {
            return null;
        }

        String username = userService.findUserById(userId).getUsername();
        channel.setTitle(username+DEFAULT_CHANNEL_NAME);

        boolean result = fcr.saveChannel(channel);
        if (result) {
            userService.addChannelInUser(userId, channel.getId());
        }
        return channel.getId();


//        Channel channel = new Channel(userId);
//        Path path = filePathUtil.getChannelFilePath(channel.getId());
//        String username;
//
//        // 유저 유효성 검사 as early return
//        if (userService.findUserById(userId) == null) {
//            return null;
//        } else{
//            username = userService.findUserById(userId).getUsername();
//        }
//
//
//        try (
//                // 위치+파일명
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos))
//        {
//            // add title
//            channel.setTitle(username + DEFAULT_CHANNEL_NAME);
//            // add channelId in User
//            userService.addChannelInUser(userId, channel.getId());
//            oos.writeObject(channel);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return channel.getId();
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Path path = filePathUtil.getChannelFilePath(channelId);

        if (!Files.exists(path)) {
            return null;
        }
        return fcr.findChannelById(channelId);

//        Path path = filePathUtil.getChannelFilePath(channelId);
//        Channel channel;
//        if (!Files.exists(path)) {
//            return null;
//        }
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
//            channel = (Channel) ois.readObject();
//            return channel;
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }


    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        Path userPath = filePathUtil.getUserFilePath(userId);

        if (!Files.exists(userPath)) {
            return new ArrayList<>();
        }
        return fcr.findChannelsByUserId(userId);

        // 유저 객체 찾아서 가져옴
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userPath.toFile()));
//            user = (User) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        List<UUID> channelIds = user.getChannelIds();
//
//        // 채널 조회 리스트 생성
//        Path channelDirectory = filePathUtil.getChannelDirectory();
//        if (Files.exists(channelDirectory)) {
//            try {
//                List<Channel> list = Files.list(channelDirectory)
//                        .filter(path -> path.toString().endsWith(".ser"))
//                        .map(path -> {
//                            try (
//                                    FileInputStream fis = new FileInputStream(path.toFile());
//                                    ObjectInputStream ois = new ObjectInputStream(fis)
//                            ) {
//                                Object data = ois.readObject();
//                                return (Channel) data;
//                            } catch (IOException | ClassNotFoundException exception) {
//                                throw new RuntimeException(exception);
//                            }
//                        }).toList();
//                List<Channel> result = new ArrayList<>();
//                for (Channel channel : list) {
//                    if (channelIds.contains(channel.getId())) {
//                        result.add(channel);
//                    }
//                }
//                return result;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return new ArrayList<>();
//        }
//
//        Path userPath = filePathUtil.getUserFilePath(userId);
//        User user;
//
//        if (!Files.exists(userPath)) {
//            return new ArrayList<>();
//        }
//
//        // 유저 객체 찾아서 가져옴
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userPath.toFile()));
//            user = (User) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        List<UUID> channelIds = user.getChannelIds();
//
//        // 채널 조회 리스트 생성
//        Path channelDirectory = filePathUtil.getChannelDirectory();
//        if (Files.exists(channelDirectory)) {
//            try {
//                List<Channel> list = Files.list(channelDirectory)
//                        .filter(path -> path.toString().endsWith(".ser"))
//                        .map(path -> {
//                            try (
//                                    FileInputStream fis = new FileInputStream(path.toFile());
//                                    ObjectInputStream ois = new ObjectInputStream(fis)
//                            ) {
//                                Object data = ois.readObject();
//                                return (Channel) data;
//                            } catch (IOException | ClassNotFoundException exception) {
//                                throw new RuntimeException(exception);
//                            }
//                        }).toList();
//                List<Channel> result = new ArrayList<>();
//                for (Channel channel : list) {
//                    if (channelIds.contains(channel.getId())) {
//                        result.add(channel);
//                    }
//                }
//                return result;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return new ArrayList<>();
//        }
    }

    @Override
    public List<Channel> findAllChannel() {
        return fcr.findAllChannel();
//        Path directory = filePathUtil.getChannelDirectory();
//
//        if (!Files.exists(directory)) {
//            return new ArrayList<>();
//        }
//
//        try {
//            List<Channel> list = Files.list(directory)
//                    .filter(path -> path.toString().endsWith(".ser"))
//                    .map(path -> {
//                        try (
//                                FileInputStream fis = new FileInputStream(path.toFile());
//                                ObjectInputStream ois = new ObjectInputStream(fis)
//                        ) {
//                            Object data = ois.readObject();
//                            return (Channel) data;
//                        } catch (IOException | ClassNotFoundException exception) {
//                            throw new RuntimeException(exception);
//                        }
//                    }).toList();
//            return list;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
    @Override
    public void updateChannelName(UUID channelId, String title) {
        fcr.updateChannelName(channelId, title);


//        Path path = filePathUtil.getChannelFilePath(channelId);
//        Channel channel;
//        if (Files.exists(path)) {
//            try {
//                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
//                channel = (Channel) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            channel.setTitle(title);
//
//            try{
//                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
//                oos.writeObject(channel);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        userService.removeChannelIdInUsers(channelId);
        fcr.deleteChannel(channelId);



//        Path path = filePathUtil.getChannelFilePath(channelId);
//        try {
//            Files.delete(path);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

//    NOT TESTED BECAUSE OF NO EXISTING MESSAGE
    @Override
    public void addMessageInChannel(UUID channelId, Message message) {
        fcr.addMessageInChannel(channelId, message);

//        Path channelPath = filePathUtil.getChannelFilePath(channelId);
//        Path messagePath = filePathUtil.getMessageFilePath(message.getId());
//        Channel channel;
//
//        if (Files.exists(messagePath)) {
//            if (Files.exists(channelPath)) {
//                try {
//                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(channelPath.toFile()));
//                    channel = (Channel) ois.readObject();
//                    if (!channel.getMessages().contains(message)) {
//                        channel.getMessages().add(message);
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//
//                try{
//                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelPath.toFile()));
//                    oos.writeObject(channel);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
    }

//    WARNING: THIS DELETES ONLY MESSAGE IN CHANNEL(NOT MESSAGE IN FILE)
//    NOW TESTED BECAUSE OF NO EXISTING MESSAGES

    @Override
    public void deleteMessageInChannel(UUID channelId, UUID messageId) {
        fcr.deleteMessageInChannel(channelId, messageId);
//        Path channelPath = filePathUtil.getChannelFilePath(channelId);
//        Channel channel;
//
//        if (Files.exists(channelPath)) {
//            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(channelPath.toFile()))) {
//
//                channel = (Channel) ois.readObject();
//                channel.getMessages().removeIf(message -> message.getId().equals(messageId));
//
//                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelPath.toFile()))) {
//                    oos.writeObject(channel);
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    @Override
    public void addUserInChannel(UUID channelId,UUID userId) {
        fcr.addUserInChannel(channelId, userId);
//        Path path = filePathUtil.getChannelFilePath(channelId);
//        Channel channel;
//
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
//            channel = (Channel) ois.readObject();
//            if (!channel.getUsersIds().contains(userId)) {
//                channel.getUsersIds().add(userId);
//            }
//            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
//                oos.writeObject(channel);
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }
}
