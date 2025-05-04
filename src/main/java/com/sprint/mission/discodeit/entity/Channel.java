package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.entity
 * fileName       : Channel2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private ChannelType type;
    private String description;

    public Channel() {
        super();
        this.name = "";
        this.description = "";
        this.type = ChannelType.PRIVATE;
    }

    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.type = ChannelType.PUBLIC;
    }


    public void setName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }
}
