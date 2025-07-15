package com.sprint.mission.discodeit.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * PackageName  : com.sprint.mission.discodeit.config
 * FileName     : QuerydslConfig
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */


@Configuration
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}