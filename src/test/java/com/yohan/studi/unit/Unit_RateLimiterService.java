package com.yohan.studi.unit;

import com.yohan.studi.cache.RateLimiterService;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Unit_RateLimiterService {
    private RateLimiterService rateLimiterService;

    @BeforeEach
    public void before() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    public void forgotPasswordConsume() {
        // get login bucket
        Bucket loginBucket = rateLimiterService.resolveBucketForgot("test@gmail.com");

        // consume once
        Assertions.assertTrue(loginBucket.tryConsume(1));

        // try consuming 1 time and assert it does not work
        Assertions.assertFalse(loginBucket.tryConsume(1));

        // get login bucket for another account
        Bucket loginBucket2 = rateLimiterService.resolveBucketForgot("test2@gmail.com");
        Assertions.assertTrue(loginBucket2.tryConsume(1));
    }

    @Test
    public void loginConsume() {
        // get login bucket
        Bucket loginBucket = rateLimiterService.resolveBucketLogin("test@gmail.com");

        // consume 10 times.
        for(int i = 0; i < 10; i++) {
            Assertions.assertTrue(loginBucket.tryConsume(1));
        }

        // try consuming 1 time and assert it does not work
        Assertions.assertFalse(loginBucket.tryConsume(1));

        // get login bucket for another account
        Bucket loginBucket2 = rateLimiterService.resolveBucketLogin("test2@gmail.com");
        Assertions.assertTrue(loginBucket2.tryConsume(1));
    }
}
