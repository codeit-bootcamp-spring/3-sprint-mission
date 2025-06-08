package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ChannelMapperIntegrationTest {

  @Autowired
  ChannelMapper channelMapper;

  @MockitoBean // DB에 테스트 값이 없으므로 Mockito Mock 빈으로 등록
  ChannelService channelService;

  @Test
  public void toDto() {
    /* 1. given (초기상태) */
    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"            // bytes (임의의 바이트 배열)
    );

    Channel channel = new Channel(ChannelType.PUBLIC, "공개채널", "공개채널입니다");

    List<User> dummyParticipants = List.of(
        new User("test", "test@gmail.com", "1234", testBinaryContent),
        new User("test2", "test2@gmail.com", "1234", null));

    when(channelService.findParticipantsByChannelId(
        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))).thenReturn(
        dummyParticipants);

    /* 2. when (행동실행) */

    ChannelDto channelDto = channelMapper.toDto(channel);

    /* 3. then (결과 검증) */
    assertNotNull(channelDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(channel.getId(), channelDto.getId());
    assertEquals(channel.getType(), channelDto.getType());
    assertEquals(channel.getName(), channelDto.getName());
    assertEquals(channel.getDescription(), channelDto.getDescription());

    // 중첩 매핑 결과 검증
//    assertEquals(channelService.findParticipantsByChannelId(
//            UUID.fromString("123e4567-e89b-12d3-a456-426614174000")).size(),
//        channelDto.getParticipants().size());
//    Assertions.assertInstanceOf(UserDto.class, channelDto.getParticipants());
  }
}
