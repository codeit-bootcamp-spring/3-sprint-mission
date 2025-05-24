package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.UserResponse;

public interface AuthService {

  /**
   * 사용자 이름과 비밀번호로 로그인한다.
   *
   * @param username 사용자 이름
   * @param password 사용자 비밀번호
   * @return UserResponse
   */
  UserResponse login(String username, String password);
}