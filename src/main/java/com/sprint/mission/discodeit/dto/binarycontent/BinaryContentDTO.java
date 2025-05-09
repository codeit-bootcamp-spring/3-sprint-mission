package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record BinaryContentDTO(String fileName, String mimeType, byte[] data) {

    public static BinaryContent toEntity(BinaryContentDTO binaryContentDTO) {
        BinaryContent binaryContent = new BinaryContent();

        binaryContent.updateFileName(binaryContentDTO.fileName());
        binaryContent.updateMimeType(binaryContentDTO.mimeType());
        binaryContent.updateData(binaryContentDTO.data());

        return binaryContent;
    }
}
