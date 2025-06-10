package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;


}
