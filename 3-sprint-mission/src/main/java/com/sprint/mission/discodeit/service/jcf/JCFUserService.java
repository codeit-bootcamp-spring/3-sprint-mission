package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private final List<User> userList;

    public JCFUserService(List<User> userList) {
        this.userList = userList;
    }

    public void create(User user) {
        userList.add(user);
    }

    public List<User> readById(String userId) {
        return userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<User> readAll() {
        return userList;
    }

    //수정 필요성 (DTO로 수정할 파라미터를 받는게 제일 깔끔할 듯, 그러나 오버라이드도 가능)
    public void update(String userId, String userName, String ModifiedUserId
            , String userPassword, String userEmail) {
        for (User u : userList) {
            if (u.getUserId().equals(userId)) {
                u.update(userName, ModifiedUserId, userPassword, userEmail); //DTO로 넘기는게 오버로디하는 것보다 효율적일 듯
            }
        }
    }

    public void deleteById(String userId) {
        userList.removeIf(u -> u.getUserId().equals(userId));
    }
}
