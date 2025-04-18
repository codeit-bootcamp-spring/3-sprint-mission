package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    /**
     * 유저명, 유저ID를 인자로 받아 유저를 생성해주는 메서드
     *
     * @Param userName 유저명
     * @Param userId 유저ID
     * @return 생성된 유저
     * @throws IllegalArgumentException 중복 ID인 유저가 존재하는 경우
     */
    @Override
    public User createUser(String userName, String userId) {
        // 중복 ID인 유저 생성 불가
        for (User user : data.values()) {
            if (user.getUserId().equals(userId)) {
                throw new IllegalArgumentException("이미 존재하는 ID입니다. 다른 ID를 입력해주세요.");
            }
        }
        // 유저 생성 및 컬렉션에 추가
        User user = new User(userName, userId);
        data.put(user.getId(),user);
        return user;
    }

    /**
     * 메모리에 저장되어있는 유저 데이터를 리턴하는 메서드
     *
     * @return 메모리에 저장된 유저데이터
     */
    @Override
    public Map<UUID, User> getUsers() {
        return data;
    }

    /**
     * 주어진 id에 해당하는 유저를 조회하는 메서드
     *
     * @param id 조회할 유저의 ID
     * @return 조회된 유저
     * @throws NoSuchElementException 해당 ID의 유저가 존재하지 않는 경우
     */
    @Override
    public User getUserById(UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다.");
        }
        return user;
    }

    /**
     * 주어진 유저ID에 해당하는 유저를 조회하는 메서드
     *
     * @param userId 조회할 유저ID
     * @return 조회된 유저
     * @throws NoSuchElementException 해당 유저ID의 유저가 존재하지 않는 경우
     */
    @Override
    public User getUserByUserId(String userId) {
        // data를 순회하며 유저 ID로 검색
        return data.values().stream()
                .filter(u->u.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("해당 유저ID의 유저를 찾을 수 없습니다."));
    }

    /**
     * 주어진 유저를 새로운 유저명으로 수정하는 메서드
     *
     * @param user 수정할 대상 유저
     * @param userName 새로운 유저명
     * @return 수정된 유저
     */
    @Override
    public User updateUser(User user, String userName) {
        User targetUser = getUserById(user.getId());
        // 유저 이름 수정
        targetUser.updateUserName(userName);
        return targetUser;
    }

    /**
     * 주어진 id에 해당하는 유저를 삭제하는 메서드
     *
     * @param id 삭제할 대상 유저 id
     * @return 삭제된 유저
     */
    @Override
    public User deleteUser(UUID id) {
        User targetUser = getUserById(id);
        // 유저 isActive값 설정 (false)
        targetUser.updateIsActive();
        // 유저 삭제
        data.remove(id);
        return targetUser;
    }

    /**
     * 유저 데이터를 저장하는 메서드
     * JCF*Service의 경우 메모리에 저장되어 있으므로 해당 메서드 구현하지 않음
     */
    @Override
    public void saveUsers() {
    }
}