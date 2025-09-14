package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 바이너리 파일의 메타데이터를 관리하는 엔티티 클래스입니다.
 * 
 * <p>파일 업로드 시 생성되며, 파일명, 크기, 타입, 상태 등의 정보를 관리합니다.
 * 실제 파일 데이터는 별도 스토리지에 저장됩니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>파일 메타데이터 관리</li>
 *   <li>업로드 상태 추적</li>
 *   <li>동적 업데이트 지원</li>
 *   <li>생성/수정 시간 자동 기록</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_binary_contents")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class BinaryContent extends BaseUpdatableEntity {

    /**
     * 원본 파일명입니다.
     * 
     * <p>사용자가 업로드한 파일의 원래 이름을 보존합니다.</p>
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * 파일의 크기입니다 (바이트 단위).
     * 
     * <p>파일 업로드 시 자동으로 계산되어 저장됩니다.</p>
     */
    @Column(name = "size", nullable = false)
    private Long size;

    /**
     * 파일의 MIME 타입입니다.
     * 
     * <p>예: image/jpeg, application/pdf, text/plain 등</p>
     */
    @Column(name = "content_type", nullable = false)
    private String contentType;

    /**
     * 파일의 현재 처리 상태입니다.
     * 
     * <p>PROCESSING, COMPLETED, FAILED 등의 값을 가질 수 있습니다.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BinaryContentStatus status;

    /**
     * 새로운 바이너리 콘텐츠를 생성합니다.
     * 
     * @param fileName 원본 파일명
     * @param size 파일 크기 (바이트)
     * @param contentType 파일의 MIME 타입
     */
    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.status = BinaryContentStatus.PROCESSING;
    }

    /**
     * 파일의 처리 상태를 업데이트합니다.
     * 
     * @param newStatus 새로운 처리 상태
     */
    public void updateStatus(BinaryContentStatus newStatus) {
        boolean anyValueUpdated = false;
        if (newStatus != null) {
            this.status = newStatus;
            anyValueUpdated = true;
        }
    }
}
