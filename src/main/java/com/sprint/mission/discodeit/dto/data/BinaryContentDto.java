package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BinaryContentDto {

  private UUID id;
  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;
}
