package org.aditya.postmorterm.notification;

import org.aditya.postmorterm.common.Topics;
import org.aditya.postmorterm.common.events.LedgerPosted;
import org.aditya.postmorterm.faults.FaultSwitchboard;
import org.aditya.postmorterm.faults.FaultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

@Component
public class ReceiptSender {

    private static final Logger log = LoggerFactory.getLogger(ReceiptSender.class);

    private final JsonMapper json;
    private final FaultSwitchboard faults;

    public ReceiptSender(JsonMapper json, FaultSwitchboard faults) {
        this.json = json;
        this.faults = faults;
    }

    @KafkaListener(topics = Topics.LEDGER_POSTED)
    public void onLedgerPosted(String payload) throws InterruptedException {
        var entry = json.readValue(payload, LedgerPosted.class);
        if (faults.isActive(FaultType.NOTIFICATION_CONSUMER_LAG)) {
            Thread.sleep(3_000);
        }
        log.info("Receipt sent orderId={} amount={} {}", entry.orderId(), entry.amount(), entry.currency());
    }
}
