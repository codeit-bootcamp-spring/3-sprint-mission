package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.*;

public class JCFUserService implements UserService {
    public final List<User> data;

    public JCFUserService(){
        this.data = new ArrayList<>();}

    @Override
    public void create(String name) {
        this.data.add(new User(name));
    }

    @Override
    public List<User> read(String id) {
        return this.data.stream()
                .filter(u -> u.getId().contains(id))
                .collect(Collectors.toList());
    }

    public List<User> readByName(String name) {
        return this.data.stream()
                .filter(u -> u.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<User>  readAll() {
        return this.data.stream()
                .collect(Collectors.toList());
    }

    @Override
    public User login(String id) {
        return this.data.stream()
                .filter(u -> u.getId().contains(id))
                .collect(Collectors.toList()).get(0);
    }

    @Override
    public void update(String id, String name) {
        this.data.stream()
                .filter(u -> u.getId().contains(id))
                .forEach(u -> {
                    u.updateById(id,name);
                    u.updateDateTime();
                });
    }

    @Override
    public void delete(String id) {
        this.data.removeIf(u -> u.getId().contains(id));
    }


}
