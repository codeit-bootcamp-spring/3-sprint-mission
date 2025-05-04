package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 프로필 이미지 & 첨부 파일 표현 도메인 모델 ( 수정 불가 : updated 필드 X )
// 참조 관계상 User, Message 모델이 BinaryContent를 참조함
@Getter
public class BinaryContent implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private final UUID id;
    private final Instant createdAt;
    //
    private String fileName;
    private byte[] fileData;
    private String fileType;
    private long fileSize;

    // 생성자
    public BinaryContent(String fileName, byte[] fileData, String fileType, long fileSize) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
