package org.aditya.postmorterm.harness.agent;

import java.util.Comparator;
import java.util.Map;

import org.aditya.postmorterm.harness.tools.Toolbox;
import org.springframework.stereotype.Component;

/**
 * Non-LLM baseline: blame the service with the most errors, falling back to the most warnings, then to the most
 * recent deploy. Strong on loud outages, expected to fail when symptoms land away from the cause.
 */
@Component
public class HeuristicBaselineAgent implements Agent {

    @Override
    public String name() {
        return "heuristic-baseline";
    }

    @Override
    public Diagnosis investigate(Toolbox tools) {
        for (String level : new String[] {"ERROR", "WARN"}) {
            var noisiest = tools.countLogsByService(level).entrySet().stream()
                    .max(Map.Entry.comparingByValue());
            if (noisiest.isPresent()) {
                var top = noisiest.get();
                return new Diagnosis(top.getKey(), null,
                        "Most %s logs (%d) in the incident window".formatted(level, top.getValue()));
            }
        }
        return tools.listDeploys().stream()
                .max(Comparator.comparing(d -> d.ts()))
                .map(d -> new Diagnosis(d.service(), null, "Most recent deploy: " + d.change()))
                .orElse(new Diagnosis(null, null, "No signal found"));
    }
}
