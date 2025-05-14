package com.sprint.mission.discodeit.entity.dto.user;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "password")
@Getter
@Setter
@NoArgsConstructor
public class UserCreateDto extends Period {

    private String username;
    private String password;
    private String email;

}
