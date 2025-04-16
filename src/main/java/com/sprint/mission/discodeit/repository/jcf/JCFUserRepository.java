package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

    private final CopyOnWriteArrayList<User> data;

    public JCFUserRepository(CopyOnWriteArrayList<User> data) {
        this.data = data;
    }

    @Override
    public void save(User user) {
        data.add(user);
    }

    @Override
    public void read() {
        data.stream()
                .forEach(System.out::println);
    }

    @Override
    public void readById(UUID id) {
        data.stream()
                .filter(user->user.getId().equals(id))
                .forEach(System.out::println);
    }

    @Override
    public void update(UUID id, User user) {
        data.stream()
                .filter(u->u.getId().equals(id))
                .forEach((u)->{
                    u.updateUpdatedAt(System.currentTimeMillis());
                    u.updateEmail(user.getEmail());
                    u.updateFriends(user.getFriends());
                    u.updatePassword(user.getPassword());
                    u.updatePhone(user.getPhone());
                    u.updateStatus(user.getStatus());
                    u.updateUserName(user.getUserName());
                    u.updateFriends(user.getFriends());
                    u.updateIsSpeakerOn(user.getIsSpeakerOn());
                    u.updateIsMikeOn(user.getIsMikeOn());
                });
    }

    @Override
    public void delete(User user) {
        data.remove(user);
    }
}
