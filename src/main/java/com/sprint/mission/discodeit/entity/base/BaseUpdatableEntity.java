package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 수정 가능한 엔티티의 기본이 되는 추상 클래스입니다.
 * 
 * <p>BaseEntity를 상속받아 수정 시간을 추가로 관리하며, JPA Auditing을 통해
 * 엔티티 수정 시점을 자동으로 기록합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>BaseEntity의 모든 기능 상속</li>
 *   <li>수정 시간 자동 기록</li>
 *   <li>JPA Auditing 지원</li>
 *   <li>수정 가능한 엔티티에 적합</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    /**
     * 엔티티가 마지막으로 수정된 시간입니다.
     * 
     * <p>JPA Auditing을 통해 자동으로 설정되며, 엔티티가 수정될 때마다 갱신됩니다.</p>
     */
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone")
    private Instant updatedAt;
}
