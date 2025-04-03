package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public class JCFUserService {

    private final List<User> userList;

    public JCFUserService(List<User> userList) {
        this.userList = userList;
    }
}
