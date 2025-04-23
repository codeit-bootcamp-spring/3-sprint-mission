package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class JCFUserService implements UserService {

    private final CopyOnWriteArrayList<User> data;


    public JCFUserService(CopyOnWriteArrayList<User> data) {
        this.data = data;
    }


    @Override
    public void create(User user) {
        data.add(user);
    }

    @Override
    public void readAll() {
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
                    u.updateUpdatedAt(Instant.now());
                    u.updateEmail(user.getEmail());
                    u.updateFriends(user.getFriends());
                    u.updatePassword(user.getPassword());
                    u.updateUserName(user.getUserName());
                    u.updateFriends(user.getFriends());
                });
    }

    @Override
    public void delete(User user) {
        data.remove(user);
    }
}
