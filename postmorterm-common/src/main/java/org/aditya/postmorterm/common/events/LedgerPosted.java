package org.aditya.postmorterm.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record LedgerPosted(String entryId, String paymentId, String orderId, BigDecimal amount, String currency,
                           Instant postedAt) {
}
