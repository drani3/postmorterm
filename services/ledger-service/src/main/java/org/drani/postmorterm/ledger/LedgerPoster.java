package org.drani.postmorterm.ledger;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.drani.postmorterm.common.Topics;
import org.drani.postmorterm.common.events.LedgerPosted;
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
public class LedgerPoster {

    private static final Logger log = LoggerFactory.getLogger(LedgerPoster.class);

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;
    private final FaultSwitchboard faults;

    // In-memory stand-in for the ledger's unique index on idempotency key.
    private final Set<String> seenKeys = ConcurrentHashMap.newKeySet();

    public LedgerPoster(KafkaTemplate<String, String> kafka, JsonMapper json, FaultSwitchboard faults) {
        this.kafka = kafka;
        this.json = json;
        this.faults = faults;
    }

    @KafkaListener(topics = Topics.PAYMENTS_CAPTURED)
    public void onPaymentCaptured(String payload) {
        var payment = json.readValue(payload, PaymentCaptured.class);
        if (faults.isActive(FaultType.LEDGER_DB_UNAVAILABLE)) {
            throw new IllegalStateException("Connection refused: ledger-db:5432");
        }
        boolean dedupe = !faults.isActive(FaultType.LEDGER_DEDUPE_DISABLED);
        if (dedupe && !seenKeys.add(payment.idempotencyKey())) {
            log.info("Duplicate capture ignored idempotencyKey={}", payment.idempotencyKey());
            return;
        }

        var entry = new LedgerPosted(UUID.randomUUID().toString(), payment.paymentId(), payment.orderId(),
                payment.amount(), payment.currency(), Instant.now());
        kafka.send(Topics.LEDGER_POSTED, entry.orderId(), json.writeValueAsString(entry));
        log.info("Ledger entry posted entryId={} orderId={} amount={} {}", entry.entryId(), entry.orderId(),
                entry.amount(), entry.currency());
    }
}
