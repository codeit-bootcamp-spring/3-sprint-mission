package com.sprint.mission.discodeit.service;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
// 일관성 유지, 유지보수 용이, 확장성 제공

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String userName, String pwd, String email, String phoneNumber, String StatusMessage);                  // 생성

    User find(UUID id);                      // 고유 식별번호로 조회

   List<User> findAll();                  // 전체 사용자 조회

    User update(UUID id, String newUserName, String newPwd, String newEmail, String newPhoneNumber, String newStatusMessage);        // 특정 사용자 정보 수정

    void delete(UUID id);                // 특정 사용자 정보 제거
}
