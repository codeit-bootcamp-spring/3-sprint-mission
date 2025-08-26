package com.sprint.mission.discodeit.security.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JwtTokenProvider 단위 테스트
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() throws JOSEException {

        // HS256 알고리즘 사용
        String accessSecret = "test-access-secret-32-bytes-minimum-1234";
        String refreshSecret = "test-refresh-secret-key-32-bytes-minimum-5678";

        int atExpMs = 60_000; // 60s
        int rtExpMs = 120_000; // 120s

        jwtTokenProvider = new JwtTokenProvider(accessSecret, atExpMs, refreshSecret, rtExpMs);
    }

    private DiscodeitUserDetails createUser(String username, Role role) {
        UUID userId = UUID.randomUUID();

        UserResponseDto response = new UserResponseDto(userId, username, username + "test.com",
            null,
            true, role);

        return new DiscodeitUserDetails(response, "pwd1234");
    }

    @Test
    void 토큰_생성_시_필수_클레임과_타입이_올바르게_설정된다() throws Exception {

        // given
        DiscodeitUserDetails principal = createUser("test", Role.USER);

        // when
        String at = jwtTokenProvider.generateAccessToken(principal); // Access Token
        String rt = jwtTokenProvider.generateRefreshToken(principal); // Refresh Token

        // then
        assertTrue(jwtTokenProvider.validateAccessToken(at));
        assertTrue(jwtTokenProvider.validateRefreshToken(rt));
        assertEquals("test", jwtTokenProvider.getUsernameFromToken(at));
        assertEquals("test", jwtTokenProvider.getUsernameFromToken(rt));
        assertThat(jwtTokenProvider.getTokenId(at)).isNotBlank();
        assertThat(jwtTokenProvider.getTokenId(rt)).isNotBlank();
        assertNotNull(jwtTokenProvider.getIssuedAt(at));
        assertNotNull(jwtTokenProvider.getExpiration(at));
    }

    @Test
    void Refresh_쿠키는_HttpOnly_Path_Max_Age가_설정된다() throws Exception {

        // given
        DiscodeitUserDetails principal = createUser("user1", Role.CHANNEL_MANAGER);
        String rt = jwtTokenProvider.generateRefreshToken(principal);

        // when
        Cookie cookie = jwtTokenProvider.generateRefreshTokenCookie(rt);

        // then
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertThat(cookie.getMaxAge()).isPositive();
    }

    @Test
    void 만료_쿠키_생성_테스트() {

        // when
        Cookie expiredCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();

        // then
        assertEquals(0, expiredCookie.getMaxAge());
    }
}