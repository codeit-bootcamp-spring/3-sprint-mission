package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.NotFoundUserException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final UserService userService;
    private final String fileName = "src/main/java/com/sprint/mission/discodeit/service/file/channels.ser";
    private final File file = new File(fileName);

    public FileChannelService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void save(Channel channel) {
        // 파일에서 읽어오기
        Map<UUID, Channel> data = readFile();

        // 존재하지 않는 사용자를 채널 주인으로 설정하는 경우 예외 처리
        if (userService.findById(channel.getChannelMaster()).isEmpty()) {
            throw new NotFoundUserException();
        }

        // Channel 저장
        data.put(channel.getId(), channel);

        joinChannel(channel);

        // Channel Data 파일에 저장
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        // 파일에서 읽어오기
        Map<UUID, Channel> data = readFile();

        // 조건에 맞는 채널 
        Optional<Channel> foundChannel = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundChannel;
    }

    @Override
    public List<Channel> findAll() {
        // 파일에서 읽어오기
        Map<UUID, Channel> data = readFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        // 파일에서 읽어오기
        Map<UUID, Channel> data = readFile();

        data.put(channel.getId(), channel);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 변경된 채널을 채널에 속해있는 User의 채널 리스트에 반영
        userService.findAll().forEach(user -> {
            List<Channel> channels = user.getChannels();
            for (int i=0; i<channels.size(); i++) {
                if (channels.get(i).equals(channel)) {
                    channels.set(i, channel);
                }
            }
            userService.update(user);
        });

        return channel;
    }

    @Override
    public void deleteById(UUID channelId) {
        // 파일에서 읽어오기
        Map<UUID, Channel> data = readFile();

        data.remove(channelId);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 채널에 속한 User의 channelList에서 해당 채널 삭제
        userService.findAll().forEach(user -> {
            List<Channel> channels = user.getChannels();
            for (int i=0; i<channels.size(); i++) {
                if (channels.get(i).getId().equals(channelId)) {
                    channels.remove(channels.get(i));
                }
            }
            userService.update(user);
        });
    }

    private Map<UUID, Channel> readFile() {
        Map<UUID, Channel> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Channel>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private void joinChannel(Channel channel) {
        userService.findById(channel.getChannelMaster()).ifPresent(user -> {
            // 채널 주인은 채널 생성 시 채널에 입장
            user.getChannels().add(channel);
            channel.getUserList().add(user);
            userService.update(user);
        });
    }
}
