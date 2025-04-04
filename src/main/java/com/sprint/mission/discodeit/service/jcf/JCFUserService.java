package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFUserService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    // 등록
    @Override
    public UUID registerUser(String username) {
        User newUser = new User(username);
        data.add(newUser);
        return newUser.getId();
    }

    // 단건 조회
    @Override
    public List<User> findUserById(UUID id) {
        List<User> users = data.stream().filter(user -> user.getId().equals(id)).collect(Collectors.toList());
        if (users.isEmpty()) {
            return new ArrayList<>();
        } else{
            return users;
        }
    }

    // 다건 조회
    @Override
    public List<User> findAllUsers() {

        return data;
    }

    // 수정
    @Override
    public void updateUsername(UUID id, String newName) {

        for (int i = 0; i < data.size(); i++) {
            User user = data.get(i);
            if (user.getId().equals(id)) {
                data.get(i).setUsername(newName);
                data.get(i).setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

//    삭제
    @Override
    public void deleteUser(UUID id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                data.remove(i);
            }
        }
    }
}
