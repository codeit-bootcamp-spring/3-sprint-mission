package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

@Schema(
        name = "Channel",
        description = "채널 정보를 담고 있는 엔티티"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_channel")
@Entity
public class Channel extends BaseUpdatableEntity {

    @Schema(
            description = "채널 타입(PUBLIC/PRIVATE)",
            type = "string",
            example = "PUBLIC"
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    @Schema(
            description = "채널 이름",
            type = "string",
            example = "일반",
            minLength = 1,
            maxLength = 100
    )
    @Column(name = "name", length = 100)
    private String name;

    @Schema(
            description = "채널 설명",
            type = "string",
            example = "일반적인 대화를 나누는 채널입니다"
    )
    @Column(name = "description", length = 1000)
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }

    @Profile("!test")
    @Converter(autoApply = true)
    public class ChannelTypePostgresConverter implements AttributeConverter<ChannelType, String> {

        @Override
        public String convertToDatabaseColumn(ChannelType attribute) {
            return attribute.name();
        }

        @Override
        public ChannelType convertToEntityAttribute(String dbData) {
            return ChannelType.valueOf(dbData);
        }
    }
}
