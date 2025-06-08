package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadStatusMapperIntegrationTest {

  @Autowired
  ReadStatusMapper readStatusMapper;


  @Test
  public void toDto() {
    /* 1. given (초기상태) */
    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"                // contentType (MIME type)

    );
    User user = new User("test", "test@gmail.com", "1234", testBinaryContent);
    Channel channel = new Channel(ChannelType.PUBLIC, "공개채널", "공개채널입니다");

    ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());

    /* 2. when (행동실행) */

    ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);

    /* 3. then (결과 검증) */
    assertNotNull(readStatusDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(readStatus.getId(), readStatusDto.id());
    assertEquals(readStatus.getChannel().getId(), readStatusDto.channelId());
    assertEquals(readStatus.getUser().getId(), readStatusDto.userId());


  }

}
