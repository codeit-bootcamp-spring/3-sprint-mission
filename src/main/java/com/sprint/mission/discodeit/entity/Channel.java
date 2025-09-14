package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

/**
 * 채널 정보를 관리하는 엔티티 클래스입니다.
 * 
 * <p>사용자들이 소통할 수 있는 공간을 정의하며, 채널 타입, 이름, 설명 등의
 * 기본 정보를 관리합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>채널 타입별 분류 (공개/비공개)</li>
 *   <li>채널명과 설명 관리</li>
 *   <li>동적 업데이트 지원</li>
 *   <li>생성/수정 시간 자동 기록</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_channels")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class Channel extends BaseUpdatableEntity {
    
    /**
     * 채널의 타입입니다.
     * 
     * <p>PUBLIC 또는 PRIVATE 값을 가지며, 채널의 접근 가능 여부를 결정합니다.</p>
     */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    /**
     * 채널의 이름입니다.
     * 
     * <p>100자 이하의 문자열로, 채널을 식별하는 데 사용됩니다.</p>
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * 채널에 대한 설명입니다.
     * 
     * <p>500자 이하의 문자열로, 채널의 목적이나 규칙 등을 설명합니다.</p>
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 새로운 채널을 생성합니다.
     * 
     * @param type 채널 타입
     * @param name 채널명
     * @param description 채널 설명
     */
    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * 채널 정보를 업데이트합니다.
     * 
     * <p>변경된 값이 있는 경우에만 수정 시간을 갱신합니다.</p>
     * 
     * @param newName 새로운 채널명 (변경하지 않으려면 null)
     * @param newDescription 새로운 채널 설명 (변경하지 않으려면 null)
     */
    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
