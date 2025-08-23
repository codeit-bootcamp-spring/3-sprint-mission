package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.SpaCsrfTokenRequestHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 최소 구성의 Spring Security 설정 클래스.
 *
 * <p>CSRF 방어 기능을 활성화하고,
 *  * SPA 환경에서 호환 가능하도록 {@link SpaCsrfTokenRequestHandler} 를 적용</p>
 *  *
 *  * <p>특징:</p>
 *  * <ul>
 *  *   <li>{@code CookieCsrfTokenRepository} 를 사용하여 브라우저 쿠키에 CSRF 토큰 저장</li>
 *  *   <li>{@code SpaCsrfTokenRequestHandler} 로 요청 헤더 기반 토큰도 인식 (SPA 클라이언트 대응)</li>
 *  *   <li>기본 인증/인가 설정은 Spring Security 기본값을 따른다 (로그인 페이지 제공 등)</li>
 *  *   <li>앱 시작 시 FilterChain에 등록된 보안 필터 목록을 디버그 로그로 출력</li>
 *  * </ul>
 *  */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security의 HTTP 보안 필터 체인을 정의
     *
     * <p>주요 설정:</p>
     * <ul>
     *   <li>CSRF 토큰을 HttpOnly=false 옵션으로 쿠키에 저장</li>
     *   <li>SPA 환경 대응: 클라이언트에서 헤더 기반으로 토큰을 보낼 경우도 허용</li>
     * </ul>
     *
     * <p>그 외 설정(요청 인증/인가, 로그인/로그아웃)은 Spring Security 기본값을 사용</p>
     *
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 시 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
        throws Exception {
        http
            // CSRF 보호 설정
            .csrf(csrf -> csrf
                // 토큰을 HttpOnly=false 쿠키에 저장
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // SPA 대응
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            );

        // http.build() → 현재 상태의 HttpSecurity를 기반으로 SecurityFilterChain 생성
        return http.build();
    }

    /**
     * 애플리케이션 시작 시 SecurityFilterChain 내 필터 정보를 디버깅 로그로 출력.
     *
     * <p>Spring Security에는 수많은 필터들이 체인 구조로 등록되며,
     * 이를 직관적으로 확인할 수 있도록 로그화 <p>
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
                .mapToObj(idx -> String.format("\t[%s/%s] %s", idx +1, filterSize,
                    filterChain.getFilters().get(idx).getClass()))
                .toList();
            log.debug("Filter Chain Debug...\n{}", String.join(System.lineSeparator(), filterNames));
        };
    }
}
