package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
/* byte[] bytes 가 엔티티에는 없고 dto는 있어서 값을 넣어주려면 불변 객체인 record는 사용 불가 */

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType
) {

}
