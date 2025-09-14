package com.sprint.mission.discodeit.exception.redis;

public class RedisLockAcquisitionException extends RuntimeException {

    public RedisLockAcquisitionException(String message) {
        super(message);
    }
}
