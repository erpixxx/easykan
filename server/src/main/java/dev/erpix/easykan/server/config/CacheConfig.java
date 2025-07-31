package dev.erpix.easykan.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import dev.erpix.easykan.server.constant.CacheKey;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(CacheKey.getCacheKeys());
        manager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(10, TimeUnit.MINUTES));
        return manager;
    }

}
