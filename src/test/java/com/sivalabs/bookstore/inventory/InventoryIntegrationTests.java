package com.sivalabs.bookstore.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.sivalabs.bookstore.TestcontainersConfiguration;
import com.sivalabs.bookstore.orders.domain.events.OrderCreatedEvent;
import com.sivalabs.bookstore.orders.domain.models.Customer;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

@ApplicationModuleTest(webEnvironment = RANDOM_PORT, classes = TestcontainersConfiguration.class)
class InventoryIntegrationTests {

    @Autowired
    private InventoryService inventoryService;

    @Test
    void handleOrderCreatedEvent(Scenario scenario) {
        var customer = new Customer("Siva", "siva@gmail.com", "9987654");
        var event = new OrderCreatedEvent(UUID.randomUUID().toString(), "P114", 2, customer);
        scenario.publish(event)
                .andWaitAtMost(Duration.ofSeconds(10))
                .andWaitForStateChange(() -> inventoryService.getStockLevel("P114"))
                .andVerify(stockLevel -> assertThat(stockLevel).isEqualTo(598));
    }
}
