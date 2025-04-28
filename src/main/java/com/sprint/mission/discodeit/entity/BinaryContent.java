package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
 * <p>
 * 수정 불가능한 도메인 모델로 간주하므로 updatedAt 필드는 정의하지 않음
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt)</li>
 *   <li>바이너리 데이터</li>
 *   <li>파일명</li>
 *   <li>MIME 타입</li>
 * </ul>
 */
@Getter
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  // 공통 정보
  private final UUID id;
  private final Instant createdAt;

  // 바이너리 데이터 관련 정보
  private final byte[] data;
  private final String fileName;
  private final String mimeType;

  // 외부에서 직접 객체 생성 방지
  private BinaryContent(byte[] data, String fileName, String mimeType) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.data = data;
    this.fileName = fileName;
    this.mimeType = mimeType;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static BinaryContent create(byte[] data, String fileName, String mimeType) {
    return new BinaryContent(data, fileName, mimeType);
  }

  @Override
  public String toString() {
    return "BinaryContent{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", fileName='" + fileName + '\'' +
        ", mimeType='" + mimeType + '\'' +
        ", dataSize=" + (data != null ? data.length : 0) + " bytes" +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinaryContent binaryContent = (BinaryContent) o;
    return Objects.equals(id, binaryContent.id) &&
        Objects.equals(fileName, binaryContent.fileName) &&
        Objects.equals(mimeType, binaryContent.mimeType) &&
        Objects.equals(createdAt, binaryContent.createdAt) &&
        Objects.deepEquals(data, binaryContent.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fileName, mimeType, createdAt, Objects.hashCode(data));
  }
}
