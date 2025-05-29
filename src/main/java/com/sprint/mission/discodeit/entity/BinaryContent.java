package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.io.Serializable;
import lombok.NoArgsConstructor;

// 프로필 이미지 & 첨부 파일 표현 도메인 모델 ( 수정 불가 : updated 필드 X )
// 참조 관계상 User, Message 모델이 BinaryContent를 참조함
@Getter
@Entity
@NoArgsConstructor
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  //
  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  // 생성자
  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}
