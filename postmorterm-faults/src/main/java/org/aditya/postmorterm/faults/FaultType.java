package org.aditya.postmorterm.faults;

/** Catalog of injectable faults. The target service is where the cause lives, not where it shows up. */
public enum FaultType {
    PAYMENT_SERVICE_CRASH("payment-service", Tier.T1_LOUD),
    LEDGER_DB_UNAVAILABLE("ledger-service", Tier.T1_LOUD),
    PAYMENT_GATEWAY_LATENCY("payment-service", Tier.T2_DEGRADED),
    NOTIFICATION_CONSUMER_LAG("notification-service", Tier.T2_DEGRADED),
    IDEMPOTENCY_KEY_DROPPED("payment-service", Tier.T3_DISTANT),
    LEDGER_DEDUPE_DISABLED("ledger-service", Tier.T3_DISTANT),
    CURRENCY_MISCONFIG_WITH_DECOY_DEPLOY("order-service", Tier.T4_MISLEADING);

    private final String targetService;
    private final Tier tier;

    FaultType(String targetService, Tier tier) {
        this.targetService = targetService;
        this.tier = tier;
    }

    public String targetService() {
        return targetService;
    }

    public Tier tier() {
        return tier;
    }
}
