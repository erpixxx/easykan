package dev.erpix.easykan;

import dev.erpix.easykan.config.EasyKanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(EasyKanProperties.class)
public class EasyKanApplication {

    // Add database health check (remove expired user tokens, etc.)
    public static void main(String[] args) {
        SpringApplication.run(EasyKanApplication.class, args);
    }

}
