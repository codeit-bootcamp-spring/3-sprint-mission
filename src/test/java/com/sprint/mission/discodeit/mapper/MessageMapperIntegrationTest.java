package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessageMapperIntegrationTest {

  @Autowired
  MessageMapper messageMapper;

  @Test
  public void toDto() {
    /* 1. given (초기상태) */
    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"                   // contentType (MIME type)

    );
    User user = new User("test", "test@gmail.com", "1234", testBinaryContent);
    Channel channel = new Channel(ChannelType.PUBLIC, "공개채널", "공개채널입니다");

    Message message = new Message(user, channel, "안녕..자니?");

    /* 2. when (행동실행) */

    MessageDto messageDto = messageMapper.toDto(message);

    /* 3. then (결과 검증) */
    assertNotNull(messageDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(message.getContent(), messageDto.content());

  }


}
