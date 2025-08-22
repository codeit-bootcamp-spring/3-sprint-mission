package com.sprint.mission.discodeit.support;

import com.sprint.mission.discodeit.config.AppConfig;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * JPA Repository 테스트용 어노테이션 (Auditing 설정 포함)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@Import(AppConfig.class)
public @interface RepositoryTest {

}

