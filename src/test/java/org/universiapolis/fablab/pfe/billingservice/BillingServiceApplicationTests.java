package org.universiapolis.fablab.pfe.billingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.config.enabled=false"
})
@ActiveProfiles("test")
class BillingServiceApplicationTests {
    @Test
    void contextLoads() {
        System.out.println("Active Profile: " + System.getProperty("spring.profiles.active"));
    }
}
