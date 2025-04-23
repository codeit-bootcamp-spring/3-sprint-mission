package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;


public class FileUserService implements UserService {

    private final String FILE_PATH = "user.ser";
    private final ChannelService channelService;

    public FileUserService(ChannelService channelService) {
        this.channelService = channelService;
    }

    private void saveToFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, User> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public User createUser(User user) {
        Map<UUID, User> users = loadFromFile();

        if (users.containsKey(user.getId())) return null;

        users.put(user.getId(), user);
        saveToFile(users);
        return user;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return Optional.ofNullable(loadFromFile().get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public boolean modifyPassword(UUID id, String password, String newPassword) {
        Map<UUID, User> users = loadFromFile();
        User user = users.get(id);

        if (user != null && user.getPassword().equals(password)) {
            user.setPassword(newPassword);
            saveToFile(users);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(UUID id) {
        Map<UUID, User> users = loadFromFile();
        if (!users.containsKey(id)) {
            System.out.println("존재하지 않는 회원입니다.");
            return false;
        }

        User user = users.get(id);

        if (!user.getChannels().isEmpty()) {
            for (Channel channel : new HashSet<>(user.getChannels())) {
                channelService.deleteChannel(channel.getId(), user);
            }
        }

        user.getChannels().forEach(channel -> {
            channel.removeMember(user);
        });

        users.remove(id);
        saveToFile(users);
        System.out.println("성공적으로 삭제되었습니다.");
        return true;
    }

}
