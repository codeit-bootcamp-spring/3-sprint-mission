package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
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
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String image;



    public BinaryContent(String image) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.image = image;

    }

    public void setImage(String image) {
        this.image = image;
        this.updatedAt = Instant.now();
    }
}
