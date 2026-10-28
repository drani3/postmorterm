package org.drani.postmorterm.common.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderPlaced(String orderId, String customerId, BigDecimal amount, String currency, Instant placedAt) {
}
