package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자에게 전송되는 알림을 관리하는 엔티티 클래스입니다.
 * 
 * <p>시스템에서 발생하는 다양한 이벤트에 대한 알림을 사용자에게 전달하며,
 * 제목과 내용을 포함한 구조화된 알림 정보를 제공합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>수신자와의 다대일 관계</li>
 *   <li>제목과 내용을 통한 구조화된 알림</li>
 *   <li>생성 시간 자동 기록</li>
 *   <li>읽음 상태 추적 가능</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    /**
     * 알림을 받는 사용자입니다.
     * 
     * <p>다대일 관계로 연결되며, 지연 로딩을 통해 성능을 최적화합니다.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * 알림의 제목입니다.
     * 
     * <p>알림의 핵심 내용을 간단히 요약하여 사용자가 빠르게 파악할 수 있도록 합니다.</p>
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 알림의 상세 내용입니다.
     * 
     * <p>알림에 대한 구체적인 정보를 포함하며, 사용자가 필요한 세부사항을 확인할 수 있습니다.</p>
     */
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 새로운 알림을 생성합니다.
     * 
     * @param receiver 알림을 받을 사용자
     * @param title 알림 제목
     * @param content 알림 내용
     */
    public Notification(User receiver, String title, String content) {
        this.receiver = receiver;
        this.title = title;
        this.content = content;
    }
}
