package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;


@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

    private final Map<String, UUID> accessIndex = new ConcurrentHashMap<>();
    private final Map<String, UUID> refreshIndex = new ConcurrentHashMap<>();

    private final int maxActiveJwtCount;
    private final JwtTokenProvider jwtTokenProvider;

    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void registerJwtInformation(JwtInformation info) {
        final UUID userId = info.userDto().id();

        origin.compute(userId, (id, queue) -> {
            if (queue == null) queue = new ConcurrentLinkedDeque<>();
            queue.offer(info);
            index(info);
            trim(queue);
            return queue;
        });
    }


    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.computeIfPresent(userId, (id, queue) -> {
            queue.forEach(this::deindex);
            queue.clear();
            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessIndex.containsKey(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshIndex.containsKey(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo) {
        UUID userId = refreshIndex.get(oldRefreshToken);
        if (userId == null) return;

        origin.computeIfPresent(userId, (id, queue) -> {
            queue.removeIf(info -> {
                if (oldRefreshToken.equals(info.refreshToken())) {
                    deindex(info);
                    return true;
                }
                return false;
            });
            queue.offer(newInfo);
            index(newInfo);
            trim(queue);
            return queue.isEmpty() ? null : queue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(info -> {
                boolean accessValid = jwtTokenProvider.validateAccessToken(info.accessToken());
                boolean refreshValid = jwtTokenProvider.validateRefreshToken(info.refreshToken());

                if (!accessValid) accessIndex.remove(info.accessToken());
                if (!refreshValid) refreshIndex.remove(info.refreshToken());

                if (!accessValid && !refreshValid) {
                    return true;
                }
                return false;
            });

            return queue.isEmpty();
        });
    }

    private void index(JwtInformation info) {
        UUID userId = info.userDto().id();
        accessIndex.put(info.accessToken(), userId);
        refreshIndex.put(info.refreshToken(), userId);
    }

    private void deindex(JwtInformation info) {
        accessIndex.remove(info.accessToken());
        refreshIndex.remove(info.refreshToken());
    }

    private void trim(Queue<JwtInformation> q) {
        while (q.size() > maxActiveJwtCount) {
            JwtInformation old = q.poll();
            if (old != null) deindex(old);
        }
    }
}
