//package com.sprint.mission.discodeit.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    /* SecurityFilterChain에서 HttpSecurit로 permitAll을 하지 않는 이유
//     * 이 방식은 여전히 Spring Securify의 다른 보안 기능들(CORS 설정, CSRF 보호 등)이 적용됨
//     * ignoring은 지정한 경로를 아예 Spring Security의 관리 대상에서 벗어나기 때문에 보안 필터 체인을 거치지 않음
//     * 때문에 CORS, CSRF 같은 보안 검사가 적용되지 않고, 인증과 권한 검사도 못함
//     * 그렇기에 WebSecurity(ignoring을 쓰는 경우)는 보안과 전혀 상관없는 API에 사용하고, 그 이외는 HttpSecurity를 사용
//     * */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return webSecurity -> webSecurity.ignoring()
//            .requestMatchers("/error"); // 이거 좀 더 분석 필요함
//    }
//}
