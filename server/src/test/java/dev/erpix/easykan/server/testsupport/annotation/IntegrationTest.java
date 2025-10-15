package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.config.DefaultTestProperties;
import dev.erpix.easykan.server.testsupport.config.TestcontainersConfig;
import jakarta.transaction.Transactional;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Composite annotation for integration tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest
@ActiveProfiles("prod")
@Import(TestcontainersConfig.class)
@DefaultTestProperties
@Transactional
public @interface IntegrationTest {

}
