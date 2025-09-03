package com.sprint.mission.discodeit.auth.jwt.store;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JwtSessionRegistry {

    private final JwtTokenRepository jwtTokenRepository;

    public JwtSessionRegistry(JwtTokenRepository jwtTokenRepository) {
        System.out.println("[JwtSessionRegistry] 생성자 호출됨: JwtTokenRepository 주입");
        this.jwtTokenRepository = jwtTokenRepository;
    }

    /**
     * 신규 토큰 메타데이터 저장. 로그인/재발급 시 발급된 AT/RT를 DB에 기록한다.
     */
    public void register(JwtTokenEntity token) {
        System.out.println("[JwtSessionRegistry] register 호출됨: jti=" + token.getJti() + ", type="
            + token.getTokenType());
        // 토큰 엔티티를 영속화한다.
        jwtTokenRepository.save(token);
        System.out.println("[JwtSessionRegistry] register 완료: 저장됨");
    }

    /**
     * 동일 username의 모든 토큰을 폐기 상태로 만든다. 동시 로그인 제한, 권한 변경 시 강제 로그아웃 구현에 사용된다.
     */
    public void revokeAllByUsername(String username) {
        System.out.println("[JwtSessionRegistry] revokeAllByUsername 호출됨: username=" + username);
        // 사용자명으로 모든 토큰을 조회한다.
        List<JwtTokenEntity> tokens = jwtTokenRepository.findByUsername(username);
        // 각 토큰에 대해 폐기 플래그를 설정한다.
        for (JwtTokenEntity t : tokens) {
            t.setRevoked(true);
        }
        // 일괄 저장으로 업데이트한다.
        jwtTokenRepository.saveAll(tokens);
        System.out.println("[JwtSessionRegistry] revokeAllByUsername 완료: count=" + tokens.size());
    }

    /**
     * 주어진 jti가 폐기되었는지 여부를 조회한다. 인증 필터가 요청 처리 전에 호출하여 접근 허용 여부를 결정한다.
     */
    @Transactional(readOnly = true)
    public boolean isRevoked(String jti) {
        boolean result = jwtTokenRepository.findById(jti)
            .map(JwtTokenEntity::isRevoked)
            .orElse(false);
        System.out.println("[JwtSessionRegistry] isRevoked 호출됨: jti=" + jti + ", result=" + result);
        return result;
    }

    /**
     * 리프레시 토큰 회전 시 이전 토큰을 폐기하고, 새 토큰의 jti를 replacedBy로 기록한다.
     */
    public void markReplaced(String oldJti, String newJti) {
        System.out.println(
            "[JwtSessionRegistry] markReplaced 호출됨: oldJti=" + oldJti + ", newJti=" + newJti);
        jwtTokenRepository.findById(oldJti).ifPresent(t -> {
            t.setRevoked(true);
            t.setReplacedBy(newJti);
            jwtTokenRepository.save(t);
            System.out.println("[JwtSessionRegistry] markReplaced 완료: oldJti 폐기 및 교체 표시");
        });
    }

    /**
     * 특정 jti의 토큰을 즉시 폐기한다. 로그아웃 등에서 사용된다.
     */
    public void revokeByJti(String jti) {
        System.out.println("[JwtSessionRegistry] revokeByJti 호출됨: jti=" + jti);
        jwtTokenRepository.findById(jti).ifPresent(t -> {
            t.setRevoked(true);
            jwtTokenRepository.save(t);
            System.out.println("[JwtSessionRegistry] revokeByJti 완료: jti=" + jti);
        });
    }
}
