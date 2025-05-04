package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

//이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
@Getter
public class BinaryContent {

    private final UUID id;
    private final Instant createdAt;
//    Instant updatedAt; // Immutable 도메인이라 updatedAt 정의x
    //
    private final String fileName;
    private final FileType fileType; // image or attachment
    private final byte[] content;


    public BinaryContent(String fileName, FileType fileType, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        //
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
    }
}
