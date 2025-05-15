package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.entity
 * fileName       : BinaryContent
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
// 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
// [ ] 수정 불가능한 도메인 모델로 간주합니다. 따라서 updatedAt 필드는 정의하지 않습니다.
// [ ] User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.
@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Instant createdAt;
    private final UUID id;

    private final String fileName;
    private final Long size;
    private final String contentType;
    private final byte[] bytes;
    private final String extension;


    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes, String extension) {
        this.createdAt = Instant.now();
        this.id = UUID.randomUUID();

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
        this.extension = extension;
    }
}
