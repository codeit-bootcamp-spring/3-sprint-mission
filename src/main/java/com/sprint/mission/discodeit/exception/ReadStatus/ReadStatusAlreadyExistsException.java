package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatusAlreadyExistsException extends ReadStatusException {

    public final UUID readStatusId;
    public final UUID userId;
    public final UUID channelId;

    public ReadStatusAlreadyExistsException(UUID readStatusId, UUID userId, UUID channelId) {
        super(
                ErrorCode.DUPLICATE_READ_STATUS,
                Map.of("readStatusId", readStatusId)
        );
        this.readStatusId = readStatusId;
        this.userId = userId;
        this.channelId = channelId;
    }

    private static Map<String, Object> buildDetails(UUID readStatusId, UUID userId,
            UUID channelId) {
        Map<String, Object> details = new HashMap<>();

        if (readStatusId != null) {
            details.put("readStatusId", readStatusId);
        }
        if (userId != null) {
            details.put("userId", userId);
        }
        if (channelId != null) {
            details.put("channelId", channelId);
        }

        return details;
    }

    public static ReadStatusAlreadyExistsException byId(UUID readStatusId) {
        return new ReadStatusAlreadyExistsException(readStatusId, null, null);
    }

    public static ReadStatusAlreadyExistsException byUserIdAndChannelId(UUID userId,
            UUID channelId) {
        return new ReadStatusAlreadyExistsException(null, userId, channelId);
    }
}
