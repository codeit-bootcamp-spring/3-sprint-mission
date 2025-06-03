package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // 직접 테이블로 매핑되지 않으며, 이를 상속받는 엔티티 클래스에 필드가 포함된다.

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false) // id UUID NOT NULL
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false) // Java 필드명은 createdAt, DB 컬럼명은 created_at으로 매핑
    private Instant createdAt;
}
