package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.entity
 * FileName     : Notification
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Entity
@Table(name = "notifications")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Column(name = "receiver_id", columnDefinition = "uuid", nullable = false)
    private UUID receiverId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
}