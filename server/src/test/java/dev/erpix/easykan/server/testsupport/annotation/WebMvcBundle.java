package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.config.SecurityConfig;
import dev.erpix.easykan.server.exception.GlobalExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@WebMvcTest
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
@TestPropertySource(properties = "easykan.oidc.enabled=false")
public @interface WebMvcBundle {

	@AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
	Class<?>[] value() default {};

}
