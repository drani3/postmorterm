package org.aditya.postmorterm.common;

/** Kafka topic names for the payments pipeline: order -> payment -> ledger -> notification. */
public final class Topics {

    public static final String ORDERS_PLACED = "orders.placed";
    public static final String PAYMENTS_CAPTURED = "payments.captured";
    public static final String LEDGER_POSTED = "ledger.posted";

    private Topics() {
    }
}
