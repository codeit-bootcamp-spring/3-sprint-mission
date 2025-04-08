package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService(List<User> data) {
        this.data = data;
    }

    @Override
    public User createUser(String userName, String userId) {
        // 중복 이름인 유저 생성 불가
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                throw new IllegalArgumentException("이미 존재하는 ID입니다. 다른 ID를 입력해주세요.");
            }
        }
        // 유저 생성
        User user = new User(userName, userId);
        // 유저 컬렉션에 추가
        data.add(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return data.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<User> getUsers() {
        return data;
    }

    @Override
    public User updateUser(User user, String userName) {
        // 유저 유효성 체크
        if (user == null || !data.contains(user)) {
            throw new NoSuchElementException("존재하지 않는 유저이므로 수정이 불가합니다.");
        }
        // 유저 이름 수정
        for (User u : data) {
            if (u.getId().equals(user.getId())) {
                u.updateUserName(userName);
                return u;
            }
        }
        return null;
    }

    @Override
    public User deleteUser(UUID id) {
        User targetUser = getUser(id);
        // 유저 유효성 검증
        if (targetUser == null || !data.contains(targetUser)) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }

        // 유저 삭제시 채널, 메시지 상의 유저는 사라지지 않고 (탈퇴) 라고 표시
        // 유저 isActive값 설정 (false)
        targetUser.updateIsActive();
        // 유저 삭제
        data.remove(targetUser);
        return targetUser;
    }

    @Override
    public User searchUserByUserId(String userId) {
        // data를 순회하며 유저 ID로 검색
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new NoSuchElementException("해당 ID의 유저를 찾을 수 없습니다.");
    }

}