package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        if (userRepository.readByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저ID입니다. 다른 유저ID를 입력해주세요.");
        };
        // 유저 생성
        User user = new User(userName, userId);
        userRepository.save(user);
        return user;
    }

    /**
     * 레포지토리로부터 읽어온 유저 데이터를 리턴하는 메서드
     *
     * @return 저장된 유저 데이터
     */
    @Override
    public Map<UUID, User> getUsers() {
        return userRepository.readAll();
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
        return userRepository.readById(id)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));
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
        return userRepository.readByUserId(userId)
                .orElseThrow(()->new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));
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
        // 유저 업데이트
        targetUser.updateUserName(userName);
        userRepository.save(targetUser);
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
        // 유저 삭제
        userRepository.delete(id);
        return targetUser;
    }

    /**
     * 유저 데이터를 레포지토리를 통해 저장하는 메서드
     */
    @Override
    public void saveUsers() {
        userRepository.saveAll();
    }
}
