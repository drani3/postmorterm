package org.aditya.postmorterm.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.aditya.postmorterm.common.Topics;
import org.aditya.postmorterm.common.events.OrderPlaced;
import org.aditya.postmorterm.faults.FaultSwitchboard;
import org.aditya.postmorterm.faults.FaultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;
    private final FaultSwitchboard faults;

    public OrderController(KafkaTemplate<String, String> kafka, JsonMapper json, FaultSwitchboard faults) {
        this.kafka = kafka;
        this.json = json;
        this.faults = faults;
    }

    public record PlaceOrder(String customerId, BigDecimal amount, String currency) {
    }

    @PostMapping
    public OrderPlaced place(@RequestBody PlaceOrder request) {
        String currency = request.currency();
        if (faults.isActive(FaultType.CURRENCY_MISCONFIG_WITH_DECOY_DEPLOY)) {
            // Bad default-currency config: amounts keep their value but change unit.
            currency = "JPY";
        }
        var event = new OrderPlaced(UUID.randomUUID().toString(), request.customerId(), request.amount(), currency,
                Instant.now());
        kafka.send(Topics.ORDERS_PLACED, event.orderId(), json.writeValueAsString(event));
        log.info("Order placed orderId={} amount={} {}", event.orderId(), event.amount(), event.currency());
        return event;
    }
}
