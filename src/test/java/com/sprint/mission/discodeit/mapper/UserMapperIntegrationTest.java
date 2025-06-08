package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperIntegrationTest {

  @Autowired
  UserMapper userMapper;

  @Test
  public void toDto() {
    /* 1. given (초기상태) */
    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"                   // contentType (MIME type)

    );
    User user = new User("test", "test@gmail.com", "1234", testBinaryContent);

    /* 2. when (행동실행) */

    UserDto userDto = userMapper.toDto(user);

    /* 3. then (결과 검증) */
    assertNotNull(userDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(user.getId(), userDto.getId());
    assertEquals(user.getEmail(), userDto.getEmail());
    assertEquals(user.getUsername(), userDto.getUsername());

    // 중첩 매핑 결과 검증
    assertNotNull(userDto.getProfile());
    assertEquals(user.getProfile().getFileName(), userDto.getProfile().getFileName());
    assertEquals(user.getProfile().getContentType(), userDto.getProfile().getContentType());
    assertEquals(user.getProfile().getSize(), userDto.getProfile().getSize());
  }

  //FIXME : binaryContent 변환 안됨.
  //Q. Dto를 Entity로 직접 변환할 일이 있을까????
//  @Test
//  public void toEntity() {
//
//    /* 1. given (초기상태) */
//    BinaryContentDto testBinaryContentDto = new BinaryContentDto(
//        UUID.randomUUID(),
//        "test_image.png",// fileName
//        2048L,                          // size in bytes
//        "image/png",                   // contentType (MIME type)
//        new byte[]{1, 2, 3, 4, 5}     // bytes (임의의 바이트 배열)
//    );
//    UserDto userDto = new UserDto(UUID.randomUUID(), "test", "test@gmail.com", testBinaryContentDto,
//        true, null, null);
//
//
//    /* 2. when (행동실행) */
//    User user = userMapper.userDtoToUser(userDto);
//    System.out.println("user = " + user);
//    System.out.println("userDto = " + userDto);
//
//    /* 3. then (결과 검증) */
//    assertNotNull(user);  // 매핑 결과가 null이 아님을 확인
//    assertEquals(user.getEmail(), userDto.email());
//    assertEquals(user.getUsername(), userDto.username());
//
//    // 중첩 매핑 결과 검증
//    assertNotNull(userDto.profile());
//    assertEquals(user.getProfile().getFileName(), userDto.profile().fileName());
//    assertEquals(user.getProfile().getContentType(), userDto.profile().contentType());
//    assertEquals(user.getProfile().getSize(), userDto.profile().size());
//  }

}
