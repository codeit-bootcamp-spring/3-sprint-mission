package com.sprint.mission.discodeit.config;

import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 최소 구성의 Spring Security 설정 클래스.
 *
 * <p>SecurityFilterChain을 기본 빌드 상태로 반환,
 * 특별한 인증 / 인가 규칙 없이 Spring Security의 기본 동작을 따름</p>
 *
 * <p>추가적으로 CommandLineRunner를 통해 애플리케이션 시작 시
 * SecurityFilterChain에 등록된 필터 목록을 디버깅 로그로 출력</p>
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security의 HTTP 보안 필터 체인을 정의.
     *
     * <p>커스텀 설정 없이 기본 SecurityFilterChain을 반환한다.
     * (모든 요청에 대해 기본 로그인 페이지 제공, CSRF 기본 적용 등)</p>
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 보안 구성 시 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
        throws Exception {
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
