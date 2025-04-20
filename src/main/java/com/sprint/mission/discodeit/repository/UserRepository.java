package com.sprint.mission.discodeit.repository;

//레포지토리 설계 및 구현
//
//[ ] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.
//[ ] 인터페이스 패키지명: com.sprint.mission.discodeit.repository
//[ ] 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository

import com.sprint.mission.discodeit.entity.User;

import java.nio.file.*;
import java.util.*;

public interface UserRepository { // 저장소에 저장해주는 것

    void create(User user); // User user 말고 Object o로도 가능한가?

    User findById(UUID id);

    List<User> findAll();

    void update(UUID id, String newName, String newEmail);

    void delete(UUID id);

//    // TODO 호출 메소드
//    // Instance의 정보가 저장된 파일의 주소를 불러온다.
//    // // 1. save와 read 메소드가 반환한 List를 파라미터로 받는다.
//    // // 2. save와 read 메소드로 저장(호출)된 파일의 경로를 불러온다
//    // // 3. String 타입으로 경로를 리턴한다.
//    String loadPath(List<Object> list);
}
