package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    /**
     * 주어진 유저를 메모리에 저장
     *
     * @param user 저장할 유저
     * @return 저장된 유저
     */
    @Override
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    /**
     * 주어진 ID에 해당하는 유저 조회
     *
     * @param id 조회할 유저 ID
     * @return 조회된 유저
     */
    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    /**
     * 메모리에 저장되어있는 유저 데이터 리턴
     *
     * @return 저장된 유저데이터
     */
    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    /**
     * 주어진 유저명에 해당하는 유저 조회
     *
     * @param userName 조회할 유저의 유저명
     * @return 조회된 유저
     */
    @Override
    public Optional<User> findByUserName(String userName) {
        return this.data.values().stream()
                .filter(u -> u.getUsername().equals(userName))
                .findFirst();
    }

    /**
     * 주어진 ID에 해당하는 유저 존재여부 판단
     *
     * @param id 유저 ID
     * @return 해당 유저 존재여부
     */
    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    /**
     * 주어진 userName에 해당하는 유저 존재여부 판단
     *
     * @param userName 유저명
     * @return 해당 유저 존재여부
     */
    @Override
    public boolean existsByUserName(String userName) {
        return this.data.values().stream()
                .anyMatch(u -> u.getUsername().equals(userName));
    }

    /**
     * 주어진 email에 해당하는 유저 존재여부 판단
     *
     * @param email 이메일
     * @return 해당 유저 존재여부
     */
    @Override
    public boolean existsByEmail(String email) {
        return this.data.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    /**
     * 주어진 ID에 해당하는 유저 삭제
     *
     * @param id 삭제할 대상 유저 ID
     */
    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
