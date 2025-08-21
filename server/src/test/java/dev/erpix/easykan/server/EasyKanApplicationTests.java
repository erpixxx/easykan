package dev.erpix.easykan.server;

import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.config.TestcontainersConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Tag(Category.INTEGRATION_TEST)
@SpringBootTest
@Import(TestcontainersConfig.class)
class EasyKanApplicationTests {

    @Test
    void contextLoads() {
    }

}
