package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users;

    public JCFUserRepository() {
        this.users = new HashMap<UUID, User>();
    }

    @Override
    public User createUser(UserCreateDto userCreateDto) {
        User user = new User(userCreateDto);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public boolean modifyUser(UserUpdateRequestDto userUpdateRequestDto) {
        if (!users.containsKey(userUpdateRequestDto.getUserId())) {
            return false;
        }
        User user = users.get(userUpdateRequestDto.getUserId());

        if (user.getPassword().equals(userUpdateRequestDto.getOldPassword())) {
            user.setPassword(userUpdateRequestDto.getNewPassword());
            return true;
        }else {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }

    }

    @Override
    public boolean deleteUser(UUID id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("존재하지 않는 유저 입니다.");
        } else {
            users.remove(id);
            return true;
        }
    }
}
