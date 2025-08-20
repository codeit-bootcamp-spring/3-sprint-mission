package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentDto(
	UUID id,
	String fileName,
	Long size,
	String contentType
) {

	// 엔티티 → DTO 변환 도우미
	public static BinaryContentDto fromEntity(BinaryContent bc) {
		if (bc == null) {
			return null;
		}
		return new BinaryContentDto(
			bc.getId(),
			bc.getFileName(),
			bc.getSize(),
			bc.getContentType()
		);
	}

}
