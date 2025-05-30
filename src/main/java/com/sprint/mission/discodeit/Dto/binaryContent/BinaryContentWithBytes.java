package com.sprint.mission.discodeit.Dto.binaryContent;

/**
 * PackageName  : com.sprint.mission.discodeit.Dto.binaryContent
 * FileName     : BinaryContentWithBytes
 * Author       : dounguk
 * Date         : 2025. 5. 31.
 */
public record BinaryContentWithBytes (
        String fileName,
        Long size,
        String contentType,
        String extension,
        byte[] bytes
){
}
