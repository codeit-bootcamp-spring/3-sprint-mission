package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserStatusMapperIntegrationTest {

  @Autowired
  UserStatusMapper userStatusMapper;

  @Test
  public void toDto() {
    /* 1. given (초기상태) */

    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"                   // contentType (MIME type)
    );
    User user = new User("test", "test@gmail.com", "1234", testBinaryContent);

    UserStatus userStatus = new UserStatus(user);


    /* 2. when (행동실행) */
    UserStatusDto userStatusDto = userStatusMapper.toDto(userStatus);

    /* 3. then (결과 검증) */
    assertNotNull(userStatusDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(userStatus.getId(), userStatusDto.id());
    assertEquals(userStatus.getLastActiveAt(), userStatusDto.lastActiveAt());

    // 중첩 매핑 결과 검증
    assertNotNull(userStatusDto.userId());
    assertEquals(userStatus.getUser().getId(), userStatusDto.userId());

  }

}
