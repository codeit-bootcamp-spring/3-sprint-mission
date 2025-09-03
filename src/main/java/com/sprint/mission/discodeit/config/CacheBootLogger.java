package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CacheBootLogger implements CommandLineRunner {

    private final CacheManager cm;

    public void run(String... args) {
        System.out.println("[CacheManager] " + cm.getClass());
    }
}