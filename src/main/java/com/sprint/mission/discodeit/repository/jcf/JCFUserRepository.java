package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class JCFUserRepository implements UserRepository {

    private final List<User> users;

    public JCFUserRepository() {
        users = new ArrayList<>();
    }

    @Override
    public void saveUser(User user) {
        users.add(user);
    }

    @Override
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getNumber() == user.getNumber()) {
                users.set(i, user);
                break;
            }
        }
    }

    @Override
    public void deleteUser(int userNumber) {
        users.removeIf(user -> user.getNumber() == userNumber);
    }

    @Override
    public User findUser(int userNumber) {
        return users.stream()
                .filter(user -> user.getNumber() == userNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAllUser() {
        return users;
    }
}