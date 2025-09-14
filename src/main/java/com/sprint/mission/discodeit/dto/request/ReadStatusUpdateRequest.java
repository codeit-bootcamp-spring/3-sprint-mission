package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadStatusUpdateRequest {

    @JsonAlias({"lastReadAt", "newLastReadAt"})
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant lastReadAt; // nullable

    @JsonAlias({"notificationEnabled", "newNotificationEnabled"})
    private Boolean notificationEnabled;

    public Instant getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(Instant v) { this.lastReadAt = v; }

    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean v) { this.notificationEnabled = v; }
}