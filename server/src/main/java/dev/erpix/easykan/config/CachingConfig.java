package dev.erpix.easykan.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import dev.erpix.easykan.constant.CacheKey;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager(CacheKey.getCacheKeys());
        manager.setCaffeine(caffeine);
        return manager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES);
    }

}
