package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

//Q. Service 레이어에서 return을 DTO로 해야하나 Entity로 해야하나?
public interface UserService {

  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  public UserDto find(UUID userId);

  public List<UserDto> find(String name);

  public List<UserDto> findAll();

  public UserDto update(UUID userId, UserUpdateRequest updateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  public void delete(UUID userId);

  public boolean hasSameEmailOrName(String name, String email);

}
