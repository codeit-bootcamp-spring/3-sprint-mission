package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(
        name = "ReadStatus",
        description = "사용자의 채널별 메시지 읽음 상태 정보를 담고 있는 엔티티"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_read_status")
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Schema(
            description = "사용자가 채널의 메시지를 마지막으로 읽은 시간",
            type = "string",
            format = "date-time",
            example = "2024-03-20T09:12:28Z"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant lastReadAt;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled;


    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = lastReadAt;
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            this.notificationEnabled = true;
        }  else {
            this.notificationEnabled = false;
        }
    }

    public void update(Instant newLastReadAt, Boolean notificationEnabled) {
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
        }
        if (notificationEnabled != null) {
            this.notificationEnabled = notificationEnabled;
        }
    }
}
