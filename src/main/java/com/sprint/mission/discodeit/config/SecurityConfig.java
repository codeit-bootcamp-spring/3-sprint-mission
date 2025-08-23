package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 최소 구성의 Spring Security 설정 클래스.
 *
 * <p>특징:</p>
 * <ul>
 *   <li>CSRF 방어 활성화 및 SPA 환경 대응</li>
 *   <li>폼 로그인 처리 및 로그인 성공/실패 핸들러 설정</li>
 *   <li>비밀번호 인코딩을 위한 {@link BCryptPasswordEncoder} 제공</li>
 *   <li>애플리케이션 시작 시 FilterChain 디버그 로그 출력</li>
 * </ul>
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security의 HTTP 보안 필터 체인 정의.
     *
     * <ul>
     *   <li>CSRF 토큰을 HttpOnly=false 쿠키에 저장</li>
     *   <li>SPA 환경에서 헤더 기반 CSRF 토큰 요청 허용</li>
     *   <li>폼 로그인 처리: 지정한 URL로 로그인 요청 처리, 성공/실패 핸들러 적용</li>
     *   <li>기타 인증/인가 설정은 기본값 사용</li>
     * </ul>
     *
     * @param http HttpSecurity 객체
     * @param loginSuccessHandler 로그인 성공 시 호출될 커스텀 핸들러
     * @param loginFailureHandler 로그인 실패 시 호출될 커스텀 핸들러
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 시 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        LoginSuccessHandler loginSuccessHandler,
        LoginFailureHandler loginFailureHandler
    )
        throws Exception {
        http
            // CSRF 보호 설정
            .csrf(csrf -> csrf
                // 토큰을 HttpOnly=false 옵션으로 쿠키에 저장
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // SPA 대응
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            // 폼 로그인 처리 과정
            .formLogin(login -> login
                // 클라이언트가 로그인 요청을 보낼 URL 지정
                .loginProcessingUrl("/api/auth/login")
                // 로그인 성공 시 호출될 커스텀 핸들러
                .successHandler(loginSuccessHandler)
                // 로그인 실패 시 호출될 커스텀 핸들러
                .failureHandler(loginFailureHandler)
            );

        // 현재 HttpSecurity 상태를 기반으로 SecurityFilterChain 생성
        return http.build();
    }

    /**
     * 애플리케이션 시작 시 SecurityFilterChain에 등록된 필터 목록을 출력.
     *
     * @param filterChain SecurityFilterChain
     * @return CommandLineRunner 실행 객체
     */
    @Bean
    public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
        return args -> {
            int filterSize = filterChain.getFilters().size();
            // 각 필터의 순서와 클래스 이름 출력
            List<String> filterNames = IntStream.range(0, filterSize)
                .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
                    filterChain.getFilters().get(idx).getClass()))
                .toList();
            log.debug("Filter Chain Debug...\n{}",
                String.join(System.lineSeparator(), filterNames));
        };
    }

    /**
     * 비밀번호 인코딩을 위한 PasswordEncoder Bean.
     *
     * <p>BCrypt 해시 알고리즘을 사용하여 안전하게 비밀번호를 저장/검증한다.</p>
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt : 내부적으로 salt를 포핳마여 매번 다른 해시 결과 생성
        return new BCryptPasswordEncoder();
    }
}
