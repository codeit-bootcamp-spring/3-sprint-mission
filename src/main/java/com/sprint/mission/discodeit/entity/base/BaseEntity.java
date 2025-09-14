package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 엔티티의 기본이 되는 추상 클래스입니다.
 * 
 * <p>공통적으로 사용되는 ID와 생성 시간을 관리하며, JPA Auditing을 통해
 * 엔티티 생성 시점을 자동으로 기록합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>UUID 기반 고유 식별자</li>
 *   <li>생성 시간 자동 기록</li>
 *   <li>JPA Auditing 지원</li>
 *   <li>상속을 통한 재사용성</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 엔티티의 고유 식별자입니다.
     * 
     * <p>UUID 타입으로 자동 생성되며, 한 번 생성된 후에는 수정할 수 없습니다.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    /**
     * 엔티티가 생성된 시간입니다.
     * 
     * <p>JPA Auditing을 통해 자동으로 설정되며, 생성 후에는 수정할 수 없습니다.</p>
     */
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
    private Instant createdAt;
}
