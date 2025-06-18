package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "channels", schema = "discodeit")
public class Channel extends BaseUpdatableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
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
}
