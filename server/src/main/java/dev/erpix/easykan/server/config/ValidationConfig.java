package dev.erpix.easykan.server.config;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }
}
