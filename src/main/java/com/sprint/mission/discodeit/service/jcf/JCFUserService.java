package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
*
* [ ] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
[ ] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
[ ] 클래스 네이밍 규칙: JCF[인터페이스 이름]
[ ] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
[ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
* */

//Q.list에 저장하는게 좋은 방법인가? map은? -> 빠른 검색을 위해 map으로 변경
public class JCFUserService implements UserService {
    private final Map<UUID, User> data; //database

    public JCFUserService(Map<UUID, User> data) {
        this.data = data;
    }

    @Override
    public void create(User user) {
        this.data.put(user.getId(), user);
    }

    @Override
    public User read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<User> read(String name) {
        List<User> matched = new ArrayList<>();
        for (Map.Entry<UUID, User> entry : data.entrySet()) {
            if (entry.getValue().getName() == name) {
                matched.add(entry.getValue());
            }
        }

        return matched;
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(this.data.values());
//        return this.data.values().stream().toList();
    }

    @Override
    public User update(UUID id, String name) {
        User selected = this.data.get(id);
        selected.update(name);
        return selected;
    }

    @Override
    public User update(UUID id, int age) {
        User selected = this.data.get(id);
        selected.update(age);
        return selected;
    }

    @Override
    public User update(UUID id, String name, int age) {
        User selected = this.data.get(id);
        selected.update(name);
        selected.update(age);
        return selected;
    }

    @Override
    public boolean delete(UUID id) {
        this.data.remove(id);

        //TODO : update return value
        return true;
    }

}
