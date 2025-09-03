package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class BinaryContentFixture {

    public static BinaryContent entity() {
        BinaryContent profile = new BinaryContent(
            "testImage",
            1L,
            "png"
        );
        ReflectionTestUtils.setField(profile, "id", UUID.randomUUID());
        return profile;
    }

    public static BinaryContentCreateRequest createRequest() {
        return new BinaryContentCreateRequest(
            "testImage",
            "png",
            "testImage".getBytes()
        );
    }

    // 프로필 이미지 DTO
    public static BinaryContentDto dto() {
        return new BinaryContentDto(
            entity().getId(),
            "testImage",
            (long) "testImage".length(),
            "png",
            BinaryContentStatus.SUCCESS
        );
    }

}
