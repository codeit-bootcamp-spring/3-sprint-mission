package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateResponse;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface UserService {

    public UserCreateResponse create(UserCreateRequest createRequest);

    public UserResponse find(UUID userId);

    public List<User> find(String name);

    public List<UserResponse> findAll();

    // QUESTION. User를 반환하는게 좋을까 response DTO를 반환하는게 좋을까
    public UserCreateResponse update(UserUpdateRequest updateRequest);

    public void delete(UUID userId);

    public boolean hasSameEmailOrName(String name, String email);

    // XXX : 불가능. userservice는 channel service에 의존하고 있지 않으므로
//    public List<User> findAttendeesByChannel(UUID channelId);
}
