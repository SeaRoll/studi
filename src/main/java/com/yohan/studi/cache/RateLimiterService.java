package com.yohan.studi.cache;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final Map<String, Bucket> loginCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotPasswordCache = new ConcurrentHashMap<>();

    public Bucket resolveBucketLogin(String key) {
        return loginCache.computeIfAbsent(key, this::newLoginBucket);
    }

    public Bucket resolveBucketForgot(String key) {
        return forgotPasswordCache.computeIfAbsent(key, this::newForgotBucket);
    }

    private Bucket newLoginBucket(String key) {
        return newBucket(10, Duration.ofMinutes(1));
    }

    private Bucket newForgotBucket(String key) {
        return newBucket(1, Duration.ofMinutes(5));
    }

    private Bucket newBucket(int amount, Duration duration) {
        Refill refill = Refill.intervally(amount, duration);
        Bandwidth limit = Bandwidth.classic(amount, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
