package com.sprint.mission.discodeit.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<MDCLoggingInterceptor> loggingFilter() {

        FilterRegistrationBean<MDCLoggingInterceptor> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new MDCLoggingInterceptor());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1); // 필터 순서 지정

        return registrationBean;
    }
}
