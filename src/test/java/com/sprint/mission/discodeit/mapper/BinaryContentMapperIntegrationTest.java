package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BinaryContentMapperIntegrationTest {

  @Autowired
  BinaryContentMapper binaryContentMapper;

  @Test
  public void toDto() {
    /* 1. given (초기상태) */
    BinaryContent testBinaryContent = new BinaryContent(
        "test_image.png",               // fileName
        2048L,                          // size in bytes
        "image/png"
    );
    /* 2. when (행동실행) */
    BinaryContentDto binaryContentDto = binaryContentMapper.toDto(
        testBinaryContent);

    /* 3. then (결과 검증) */
    assertNotNull(binaryContentDto);  // 매핑 결과가 null이 아님을 확인
    assertEquals(testBinaryContent.getFileName(), binaryContentDto.getFileName());
    assertEquals(testBinaryContent.getSize(), binaryContentDto.getSize());
    assertEquals(testBinaryContent.getContentType(), binaryContentDto.getContentType());

  }
}
