package dev.erpix.easykan.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class EasyKanApplicationTests {

    @Test
    void contextLoads() {
    }

}
