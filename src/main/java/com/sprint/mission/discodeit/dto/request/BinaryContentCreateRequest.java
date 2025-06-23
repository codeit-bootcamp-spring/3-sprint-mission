package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
		@NotBlank(message = "파일 이름은 필수입니다.") @Size(max = Constants.Database.FILENAME_MAX_LENGTH, message = "파일 이름은 255자를 초과할 수 없습니다.") String fileName,

		@NotBlank(message = "콘텐츠 타입은 필수입니다.") @Size(max = Constants.Database.CONTENT_TYPE_MAX_LENGTH, message = "콘텐츠 타입은 100자를 초과할 수 없습니다.") String contentType,

		@NotNull(message = "파일 데이터는 필수입니다.") byte[] bytes) {

}
