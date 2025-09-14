package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 채널 내에서 사용자가 작성한 메시지를 관리하는 엔티티 클래스입니다.
 * 
 * <p>메시지의 내용, 작성자, 첨부파일 등을 관리하며, 채널과 사용자와의 관계를
 * 설정합니다.</p>
 * 
 * <p>주요 특징:</p>
 * <ul>
 *   <li>메시지 내용과 첨부파일 관리</li>
 *   <li>채널과 작성자와의 다대일 관계</li>
 *   <li>첨부파일과의 일대다 관계</li>
 *   <li>캐스케이드 삭제 및 수정 시간 자동 기록</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Entity
@Table(name = "tbl_messages")
@Getter
@NoArgsConstructor
@DynamicUpdate
public class Message extends BaseUpdatableEntity {

    /**
     * 메시지의 텍스트 내용입니다.
     * 
     * <p>null이 허용되지 않으며, 메시지의 핵심 정보를 담고 있습니다.</p>
     */
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 메시지가 작성된 채널입니다.
     * 
     * <p>채널이 삭제되면 해당 채널의 모든 메시지도 함께 삭제됩니다.</p>
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", columnDefinition = "uuid")
    @OnDelete(action = OnDeleteAction.CASCADE) // ON DELETE CASCADE
    private Channel channel;

    /**
     * 메시지를 작성한 사용자입니다.
     * 
     * <p>사용자가 삭제되면 작성자 정보는 null로 설정됩니다.</p>
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", columnDefinition = "uuid")
    @OnDelete(action = OnDeleteAction.SET_NULL) // ON DELETE SET NULL
    private User author;

    /**
     * 메시지에 첨부된 파일들의 목록입니다.
     * 
     * <p>메시지가 삭제되면 첨부파일도 함께 제거되며, 고아 객체는 자동으로 정리됩니다.</p>
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
        name = "tbl_message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    /**
     * 새로운 메시지를 생성합니다.
     * 
     * @param content 메시지 내용
     * @param channel 메시지가 작성될 채널
     * @param author 메시지 작성자
     * @param attachments 첨부파일 목록
     */
    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments;
    }

    /**
     * 메시지 내용을 업데이트합니다.
     * 
     * <p>내용이 변경된 경우에만 수정 시간을 갱신합니다.</p>
     * 
     * @param newContent 새로운 메시지 내용
     */
    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }
}
