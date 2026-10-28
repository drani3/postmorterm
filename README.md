# Postmorterm

A benchmark for AI agents diagnosing incidents in a payments microservices system. Faults are injected into a
small Kafka pipeline, each incident is frozen into an evidence bundle, and agents are scored against the known cause.

```
order-service ──orders.placed──▶ payment-service ──payments.captured──▶ ledger-service ──ledger.posted──▶ notification-service
   :8081                            :8082                                 :8083                            :8084
```

## Modules

| Module | Purpose |
|---|---|
| `postmorterm-common` | Event records and topic names |
| `postmorterm-faults` | Fault catalog (`FaultType`, `Tier`) and the `/_faults` switchboard every service embeds |
| `postmorterm-bundle` | Evidence bundle model and `BundleStore` reader |
| `services/*` | The four payments services, with fault hooks at the points where each fault applies |
| `postmorterm-harness` | `Toolbox` (the agent's only view of the evidence), `Agent`, heuristic baseline, `Scorer`, `BenchmarkRunner` |
| `bundles/` | Captured incidents; `sample-001` is a hand-written T3 example |

## Bundle layout

```
bundles/<incident>/
  manifest.json      alert + window, shown to the agent
  logs.jsonl         LogLine per line
  spans.jsonl        Span per line
  metrics.jsonl      MetricPoint per line
  deploys.jsonl      DeployEvent per line
  ground_truth.json  injected cause, hidden from agents
```

## Running

```sh
./mvnw package -DskipTests
java -jar postmorterm-harness/target/postmorterm-harness-0.0.1-SNAPSHOT.jar --bundles=bundles

# live system
docker compose up -d
java -jar services/order-service/target/order-service-0.0.1-SNAPSHOT.jar   # likewise payment, ledger, notification
curl -X POST localhost:8081/orders -H 'Content-Type: application/json' -d '{"customerId":"c-1","amount":40.00,"currency":"USD"}'
curl -X POST localhost:8082/_faults/IDEMPOTENCY_KEY_DROPPED
```

## Next steps

- Load generator plus scenario runner: start the stack, apply traffic, inject a fault, capture a bundle
- Bundle capture: logs as JSON, traces via OpenTelemetry, metrics snapshots, deploy events
- LLM agent that exposes `Toolbox` methods as tools (Claude API)
- Reports broken down by tier, model and failure mode
