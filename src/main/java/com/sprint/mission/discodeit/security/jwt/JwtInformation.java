package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.*;

/**
 * PackageName  : com.sprint.mission.discodeit.security.jwt
 * FileName     : JwtInformation
 * Author       : dounguk
 * Date         : 2025. 8. 21.
 */
@Data
@AllArgsConstructor
public class JwtInformation {

    private UserDto userDto;
    private String accessToken;
    private String refreshToken;

    public void rotate(String newAccessToken, String newRefreshToken) {
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
    }
}
