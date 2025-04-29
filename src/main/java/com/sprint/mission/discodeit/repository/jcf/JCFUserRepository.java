package com.sprint.mission.discodeit.repository.jcf;

//[ ] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.
//[ ] 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf
//[ ] 클래스 네이밍 규칙: JCF[인터페이스 이름]
//        [ ] 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Path;
import java.security.Key;
import java.util.*;

public class JCFUserRepository implements UserRepository { // JCF를 활용한 저장로직

    private final Map<UUID, User> userMap;

    public JCFUserRepository() {
        this.userMap = new HashMap(); // 주소와 파일을 받는 HashMap // Q. 이럴 때는 objectMap 대신 userMap이라고 해야 하는가? Map을 쓴 건 옳았나?
    }

    public void create(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList(userMap.values());
    }

    @Override
    public void update(UUID id, String newName, String newEmail) {
        User user = this.userMap.get(id);
        if (user != null) {
            user.updateUser(newName, newEmail);
        }
    }

    @Override
    public void delete(UUID id) {
//        if (!this.userMap.containsKey(id)) {
//            throw new NoSuchElementException(id + "ID를 가진 채널을 찾을 수 없습니다."); // 비즈니스 로직
//        }
        this.userMap.remove(id);
    }
}
