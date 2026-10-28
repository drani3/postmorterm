package org.drani.postmorterm.payment;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.drani.postmorterm.common.Topics;
import org.drani.postmorterm.common.events.OrderPlaced;
import org.drani.postmorterm.common.events.PaymentCaptured;
import org.drani.postmorterm.faults.FaultSwitchboard;
import org.drani.postmorterm.faults.FaultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

@Component
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    /** Share of captures where the simulated gateway times out and the capture is retried. */
    private static final double GATEWAY_RETRY_RATE = 0.1;

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;
    private final FaultSwitchboard faults;

    public PaymentProcessor(KafkaTemplate<String, String> kafka, JsonMapper json, FaultSwitchboard faults) {
        this.kafka = kafka;
        this.json = json;
        this.faults = faults;
    }

    @KafkaListener(topics = Topics.ORDERS_PLACED)
    public void onOrderPlaced(String payload) throws InterruptedException {
        var order = json.readValue(payload, OrderPlaced.class);
        if (faults.isActive(FaultType.PAYMENT_SERVICE_CRASH)) {
            throw new IllegalStateException("Payment gateway client not initialised");
        }
        if (faults.isActive(FaultType.PAYMENT_GATEWAY_LATENCY)) {
            Thread.sleep(ThreadLocalRandom.current().nextLong(2_000, 6_000));
        }

        capture(order);
        if (ThreadLocalRandom.current().nextDouble() < GATEWAY_RETRY_RATE) {
            log.warn("Gateway timeout for orderId={}, retrying capture", order.orderId());
            capture(order);
        }
    }

    private void capture(OrderPlaced order) {
        // The key must be stable across retries so the ledger can dedupe them.
        String idempotencyKey = faults.isActive(FaultType.IDEMPOTENCY_KEY_DROPPED)
                ? UUID.randomUUID().toString()
                : "pay-" + order.orderId();
        var event = new PaymentCaptured(UUID.randomUUID().toString(), order.orderId(), idempotencyKey,
                order.amount(), order.currency(), Instant.now());
        kafka.send(Topics.PAYMENTS_CAPTURED, event.orderId(), json.writeValueAsString(event));
        log.info("Payment captured paymentId={} orderId={}", event.paymentId(), event.orderId());
    }
}
