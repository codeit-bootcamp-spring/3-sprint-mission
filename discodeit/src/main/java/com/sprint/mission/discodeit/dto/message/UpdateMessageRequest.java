package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record UpdateMessageRequest( UUID id,
                                    String newContent,
                                    List<UUID> attachmentIds)  {
}
