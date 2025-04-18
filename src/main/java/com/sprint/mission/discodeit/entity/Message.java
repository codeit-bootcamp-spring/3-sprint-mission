package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msgContent;  // 메시지 내용
    private User sendUser;      // 작성자
    private Channel sendChannel;    // 작성 채널
    private boolean isUpdated;  // 메시지 수정 여부
    private boolean isDeleted;  // 메시지 삭제 여부

    public Message(Channel sendChannel, User sendUser, String msgContent) {
        this.msgContent = msgContent;
        this.sendUser = sendUser;
        this.sendChannel = sendChannel;
        this.isUpdated = false;
        this.isDeleted = false;
    }

    public Channel getSendChannel() {
        return sendChannel;
    }

    public User getSendUser() {
        return sendUser;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public boolean getIsUpdated() {
        return isUpdated;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void updateMsgContent(String msgContent) {
        this.msgContent = msgContent;
        this.isUpdated = true;
        this.updateTimestamp();
    }

    public void deleteMsgContent() {
        this.msgContent = "";
        this.isDeleted = true;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgContent='" + msgContent + '\'' + (isUpdated ? "(수정됨)" : "") + (isDeleted ? "(삭제된 메세지입니다.)" : "") +
                ", sendUser=" + sendUser.getUserName() + "("+ sendUser.getUserId() +")"+ (sendUser.getIsActive() ? "" : "(탈퇴)") +
                ", sendChannel=" + (sendChannel != null ? sendChannel.getChannelName() : "null") +
                ", createdAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getCreatedAt()) +
                ", updatedAt=" + new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(getUpdatedAt()) +
                ", id=" + getId() +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
