package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final List<User> data;


    public JCFUserService(List<User> data) {
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
                .filter(u->u.getId().equals(id))
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
