package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Column(name = "receiver_id", columnDefinition = "uuid", nullable = false)
    private UUID receiverId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Builder
    private Notification(UUID receiverId, String title, String content) {
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
    }
}
