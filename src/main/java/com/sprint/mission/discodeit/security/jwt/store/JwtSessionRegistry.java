package com.sprint.mission.discodeit.security.jwt.store;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class JwtSessionRegistry {

    private static final String SERVICE_NAME = "[JwtSessionRegistry] ";

    private final JwtTokenRepository jwtTokenRepository;

    public JwtSessionRegistry(JwtTokenRepository jwtTokenRepository) {
        log.info(SERVICE_NAME + "생성자 호출됨: JwtTokenRepository 주입");
        this.jwtTokenRepository = jwtTokenRepository;
    }

    public void register(JwtTokenEntity token) {
        log.info(SERVICE_NAME + "register 호출됨: jti= {}, type={}", token.getJti(), token.getTokenType());

        jwtTokenRepository.save(token);
        log.info(SERVICE_NAME + "register 완료: 토큰 저장됨");
    }

    public void revokeAllByUsername(String username) {
        log.info(SERVICE_NAME + "revokeAllByUsername 호출됨: username= {}", username);

        List<JwtTokenEntity> jwtTokens = jwtTokenRepository.findByUsername(username);

        for (JwtTokenEntity token : jwtTokens) {
            token.setRevoked(true);
        }

        jwtTokenRepository.saveAll(jwtTokens);
        log.info(SERVICE_NAME + "revokeAllByUsername 완료: count= {}", jwtTokens.size());
    }

    @Transactional(readOnly = true)
    public boolean isRevoked(String jti) {
        boolean result = jwtTokenRepository.findById(jti)
                .map(JwtTokenEntity::isRevoked)
                .orElse(false);

        log.info(SERVICE_NAME + "isRevoked 호출됨: jti= {} result= {}", jti, result);

        return result;
    }

    public void markReplaced(String oldJti, String newJti) {
        log.info(SERVICE_NAME + "markReplaced 호출됨: oldJti= {}, newJti= {}", oldJti, newJti);

        jwtTokenRepository.findById(oldJti).ifPresent(jwtToken -> {
            jwtToken.setRevoked(true);
            jwtToken.setReplacedBy(newJti);
            jwtTokenRepository.save(jwtToken);
            log.info(SERVICE_NAME + "markReplaced 완료: oldJti 폐기 및 교체 표시");
        });
    }

    public void revokedByJti(String jti) {
        log.info(SERVICE_NAME + "revokedByJti 호출됨: jti={}", jti);
        jwtTokenRepository.findById(jti).ifPresent(jwtToken -> {
            jwtToken.setRevoked(true);
            jwtTokenRepository.save(jwtToken);
            log.info(SERVICE_NAME + "revokedByJti 완료: jti= {}", jti);
        });
    }
}
