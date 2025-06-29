package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@code WebMvcConfig} 클래스는 Spring MVC의 설정을 커스터마이징하는 구성 클래스입니다.
 *
 * <p>본 설정 클래스에서는 {@link MDCLoggingInterceptor}를 스프링의 핸들러 인터셉터 체인에 등록하여,
 * 모든 HTTP 요청에 대해 MDC 기반의 로깅 정보를 자동으로 설정할 수 있도록 합니다.</p>
 *
 * <p>{@code WebMvcConfigurer}를 구현함으로써 {@code addInterceptors} 메서드를 오버라이딩하여
 * 커스텀 인터셉터를 MVC 요청 처리 흐름에 삽입할 수 있게 됩니다.</p>
 *
 * <p>※ 단, 이 설정은 {@link MDCLoggingInterceptor}가 {@code HandlerInterceptor} 인터페이스의
 * 구현체로서 동작하는 경우에만 의미가 있으며, 현재의 MDC 처리 로직이 {@code OncePerRequestFilter}로만 구성되어 있다면,
 * 이 설정은 영향을 미치지 않습니다.</p>
 *
 * @author 폐하
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 커스텀 인터셉터를 Spring MVC 인터셉터 체인에 등록합니다.
     *
     * <p>{@code /**} 패턴을 통해 모든 요청 URI에 대해 {@code MDCLoggingInterceptor}가 적용됩니다.</p>
     *
     * @param registry 스프링 MVC의 인터셉터 레지스트리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MDCLoggingInterceptor()) // 커스텀 인터셉터 인스턴스 등록
                .addPathPatterns("/**"); // 모든 요청 경로에 적용
    }
}
