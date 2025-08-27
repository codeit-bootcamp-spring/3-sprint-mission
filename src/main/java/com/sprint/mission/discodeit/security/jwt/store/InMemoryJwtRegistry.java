package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 메모리에 JWT 정보를 저장하는 JWT 레지스트리 구현체입니다.
 * 
 * <p>이 클래스는 JWT 토큰의 상태를 메모리에서 관리하며, 다음과 같은 특징을 가집니다:</p>
 * <ul>
 *   <li>동시성 처리를 위해 ConcurrentHashMap과 ConcurrentLinkedQueue 사용</li>
 *   <li>사용자별로 최대 동시 로그인 수 제한</li>
 *   <li>토큰 유효성 검증과 레지스트리 존재 여부를 함께 확인</li>
 *   <li>만료된 토큰의 자동 정리</li>
 * </ul>
 * 
 * <p>사용 예시:</p>
 * <pre>{@code
 * @Autowired
 * private InMemoryJwtRegistry jwtRegistry;
 * 
 * // 토큰 등록
 * JwtInformation info = new JwtInformation(userDto, accessToken, refreshToken);
 * jwtRegistry.registerJwtInformation(info);
 * 
 * // 토큰 존재 여부 확인
 * boolean exists = jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken);
 * }</pre>
 * 
 * @author Discodeit Team
 * @since 1.0.0
 * @see JwtRegistry
 * @see JwtInformation
 * @see JwtTokenProvider
 */
@Slf4j
@Service
public class InMemoryJwtRegistry implements JwtRegistry {

    /** 서비스 이름을 나타내는 상수 */
    private static final String SERVICE_NAME = "[InMemoryJwtRegistry] ";

    /**
     * 사용자별 JWT 정보를 저장하는 메모리 저장소
     * Key: 사용자 ID (UUID)
     * Value: 해당 사용자의 JWT 정보 큐
     */
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

    /** 최대 동시 활성 JWT 개수 */
    private final int maxActiveJwtCount;

    /** JWT 토큰 유효성 검증을 위한 프로바이더 */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * InMemoryJwtRegistry를 생성합니다.
     * 
     * @param maxActiveJwtCount 최대 동시 활성 JWT 개수 (기본값: 1)
     * @param jwtTokenProvider JWT 토큰 유효성 검증을 위한 프로바이더
     */
    public InMemoryJwtRegistry(
            @Value("${jwt.max-active-count:1}") int maxActiveJwtCount,
            JwtTokenProvider jwtTokenProvider) {
        this.maxActiveJwtCount = maxActiveJwtCount;
        log.info(SERVICE_NAME + "생성자 호출됨: maxActiveJwtCount={}", maxActiveJwtCount);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>동시 로그인 제한을 위해 최대 개수를 초과하는 경우 
     * 오래된 토큰을 자동으로 제거합니다.</p>
     * 
     * @param jwtInformation 등록할 JWT 정보
     */
    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();
        String username = jwtInformation.userDto().username();

        log.info(SERVICE_NAME + "토큰 등록 시작: userId={}, username={}",
                userId, username);

        // 1. 사용자별 큐 가져오기 (없으면 새로 생성)
        Queue<JwtInformation> userTokens = origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());

        // 2. 동시 로그인 제한: 최대 개수 초과 시 오래된 토큰 제거
        while (userTokens.size() >= maxActiveJwtCount) {
            JwtInformation removedToken = userTokens.poll();
            if (removedToken != null) {
                log.info(SERVICE_NAME + "동시 로그인 제한: 오래된 토큰 제거 - userId={}, username={}",
                        userId, username);
            }
        }

        // 3. 새 토큰 등록
        userTokens.offer(jwtInformation);
        log.info(SERVICE_NAME + "새 토큰 등록됨: userId={}, username={}, 현재 활성 토큰 수 ={}",
                userId, username, userTokens.size());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>사용자 ID에 해당하는 모든 JWT 정보를 메모리에서 제거합니다.</p>
     * 
     * @param userId 무효화할 사용자의 ID
     */
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        if (userId == null) {
            log.warn(SERVICE_NAME + "userId가 null이므로 무효화할 수 없음");
            return;
        }

        Queue<JwtInformation> removedTokens = origin.remove(userId);
        if (removedTokens != null && !removedTokens.isEmpty()) {
            log.info(SERVICE_NAME + "사용자별 모든 JWT 정보 삭제됨: userId={}, 삭제된 토큰 수={}",
                    userId, removedTokens.size());
        } else {
            log.debug(SERVICE_NAME + "삭제할 JWT 정보 없음: userId={}", userId);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>사용자의 로그인 상태를 판단할 때 활용됩니다.</p>
     * 
     * @param userId 확인할 사용자의 ID
     * @return 활성 JWT 정보가 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> userTokens = origin.get(userId);
        boolean hasActive = userTokens != null && !userTokens.isEmpty();

        log.info(SERVICE_NAME + "사용자별 활성 JWT 정보 존재 여부: user={}, hasActive={}", userId, hasActive);

        if (hasActive) {
            log.debug(SERVICE_NAME + "활성 토큰 수: {}", userTokens.size());
        }

        return hasActive;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Access Token의 유효성을 먼저 검증한 후, 레지스트리에 존재하는지 확인합니다.
     * 필터에서 유효한 토큰인지 확인할 때 활용됩니다.</p>
     * 
     * @param accessToken 확인할 Access Token
     * @return 토큰이 유효하고 레지스트리에 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        boolean found = false;
        UUID foundUserId = null;

        // 먼저 토큰 유효성 검증
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            log.debug(SERVICE_NAME + "유효하지 않은 Access Token= {}", accessToken);
            return found;
        }

        // 레지스트리에서 토큰 존재 여부 확인
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userTokens = entry.getValue();

            for (JwtInformation jwtInformation : userTokens) {
                if (accessToken.equals(jwtInformation.accessToken())) {
                    found = true;
                    foundUserId = userId;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        log.info(SERVICE_NAME + "AccessToken별 활성 JWT 정보 존재 여부: accessToken={}, found={}, userId={}",
                accessToken.substring(0, Math.min(10, accessToken.length())), found, foundUserId);

        return found;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Refresh Token의 유효성을 먼저 검증한 후, 레지스트리에 존재하는지 확인합니다.
     * 토큰 재발급 시 유효한 토큰인지 확인할 때 활용됩니다.</p>
     * 
     * @param refreshToken 확인할 Refresh Token
     * @return 토큰이 유효하고 레지스트리에 존재하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        boolean found = false;
        UUID foundUserId = null;

        // 먼저 토큰 유효성 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.debug(SERVICE_NAME + "유효하지 않은 Refresh Token= {}", refreshToken);
            return found;
        }
        
        // 레지스트리에서 토큰 존재 여부 확인
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userTokens = entry.getValue();

            for (JwtInformation jwtInformation : userTokens) {
                if (refreshToken.equals(jwtInformation.refreshToken())) {
                    found = true;
                    foundUserId = userId;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        log.info(SERVICE_NAME + "RefreshToken별 활성 JWT 정보 존재 여부: refreshToken={}, found={}, userId={}",
                refreshToken.substring(0, Math.min(10, refreshToken.length())), found, foundUserId);

        return found;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>기존 Refresh Token을 새로운 JWT 정보로 교체합니다.
     * 토큰 재발급 시 기존 토큰을 무효화하고 새 토큰을 등록합니다.</p>
     * 
     * @param refreshToken 교체할 기존 Refresh Token
     * @param newJwtInformation 새로운 JWT 정보
     */
    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID targetUserId = null;
        JwtInformation oldToken = null;

        // 기존 Refresh Token을 가진 사용자와 토큰 찾기
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userTokens = entry.getValue();

            for (JwtInformation jwtInformation : userTokens) {
                if (refreshToken.equals(jwtInformation.refreshToken())) {
                    targetUserId = userId;
                    oldToken = jwtInformation;
                    break;
                }
            }
            if (targetUserId != null) break;
        }

        if (targetUserId != null && oldToken != null) {
            // 기존 토큰 제거 후 새 토큰 등록
            Queue<JwtInformation> userTokens = origin.get(targetUserId);
            userTokens.remove(oldToken);
            userTokens.offer(newJwtInformation);

            log.info(SERVICE_NAME + "JWT 정보 로테이션 완료: userId={}, 기존 토큰 제거됨, 새 토큰 등록됨", targetUserId);
        } else {
            log.warn(SERVICE_NAME + "JWT 정보 로테이션 실패: refreshToken을 찾을 수 없음");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>만료된 JWT 정보를 메모리에서 정리합니다.
     * Access Token과 Refresh Token의 유효성을 검증하여 
     * 둘 중 하나라도 유효한 경우에만 유지합니다.</p>
     * 
     * <p>정기적으로 호출하여 메모리 사용량을 최적화하고 
     * 보안을 강화하는 것을 권장합니다.</p>
     */
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        int totalRemoved = 0;

        Iterator<Map.Entry<UUID, Queue<JwtInformation>>> iterator = origin.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Queue<JwtInformation>> entry = iterator.next();
            UUID userId = entry.getKey();
            Queue<JwtInformation> userTokens = entry.getValue();

            // 빈 큐 처리
            if (userTokens.isEmpty()) {
                iterator.remove();
                totalRemoved++;
                log.debug(SERVICE_NAME + "빈 큐 제거: userId={}", userId);
                continue;
            }

            // 유효한 토큰만 유지
            Queue<JwtInformation> kept = new ConcurrentLinkedQueue<>();
            for (JwtInformation jwtInformation : userTokens) {
                boolean accessValid = jwtTokenProvider.validateAccessToken(jwtInformation.accessToken());
                boolean refreshValid = jwtTokenProvider.validateRefreshToken(jwtInformation.refreshToken());

                // Access Token 또는 Refresh Token 중 하나라도 유효하면 유지
                if (accessValid || refreshValid) {
                    kept.offer(jwtInformation);
                }
            }

            // 유효한 토큰이 없으면 사용자 제거, 있으면 업데이트
            if (kept.isEmpty()) {
                iterator.remove();
            } else {
                origin.put(userId, kept);
            }
        }

        log.info(SERVICE_NAME + "만료된 JWT 정보 정리 완료: 제거된 사용자 수={}, 남은 사용자 수={}",
                totalRemoved, origin.size());
    }

    public UUID findUserIdByRefreshToken(String refreshToken) {
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userTokens = entry.getValue();

            for (JwtInformation info : userTokens) {
                if (refreshToken.equals(info.refreshToken())) {
                    return userId;
                }
            }
        }
        log.info(SERVICE_NAME + "해당 유저가 존재하지 않음");

        return null;
    }
}
