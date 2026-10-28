package org.aditya.postmorterm.common.events;

import java.math.BigDecimal;
import java.time.Instant;

/** The ledger dedupes on {@code idempotencyKey}; faults that break it surface as duplicate charges downstream. */
public record PaymentCaptured(String paymentId, String orderId, String idempotencyKey, BigDecimal amount,
                              String currency, Instant capturedAt) {
}
