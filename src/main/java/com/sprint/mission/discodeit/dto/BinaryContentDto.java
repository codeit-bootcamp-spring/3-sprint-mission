package com.sprint.mission.discodeit.dto;

import java.util.Arrays;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/* byte[] bytes 가 엔티티에는 없고 dto는 있어서 값을 넣어주려면 불변 객체인 record는 사용 불가 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BinaryContentDto {

  private UUID id;
  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;

  @Override
  public String toString() {
    return "BinaryContentDto{" +
        "id=" + id +
        ", fileName='" + fileName + '\'' +
        ", size=" + size +
        ", contentType='" + contentType + '\'' +
        ", bytes=" + Arrays.toString(bytes) +
        '}';
  }

}
