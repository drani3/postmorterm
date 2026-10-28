package org.drani.postmorterm.harness.runner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.drani.postmorterm.bundle.BundleStore;
import org.drani.postmorterm.harness.agent.Agent;
import org.drani.postmorterm.harness.scoring.Scorer;
import org.drani.postmorterm.harness.tools.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Runs every agent against every bundle under {@code --bundles} (default {@code bundles/}) and prints scores. */
@Component
public class BenchmarkRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkRunner.class);

    private final BundleStore store;
    private final List<Agent> agents;
    private final Scorer scorer;

    public BenchmarkRunner(BundleStore store, List<Agent> agents, Scorer scorer) {
        this.store = store;
        this.agents = agents;
        this.scorer = scorer;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path root = Path.of(args.containsOption("bundles") ? args.getOptionValues("bundles").get(0) : "bundles");
        System.out.printf("%-16s %-20s %-14s %-22s %-6s %s%n", "incident", "agent", "tier", "predicted", "score",
                "tool calls");
        for (Path dir : bundleDirs(root)) {
            var bundle = store.load(dir);
            var truth = store.groundTruth(dir);
            for (Agent agent : agents) {
                var tools = new Toolbox(bundle);
                var diagnosis = agent.investigate(tools);
                var score = scorer.score(diagnosis, truth);
                System.out.printf("%-16s %-20s %-14s %-22s %-6.1f %d%n", bundle.manifest().incidentId(),
                        agent.name(), truth.tier(), diagnosis.service(), score.points(), tools.totalCalls());
                log.debug("{} rationale: {}", agent.name(), diagnosis.rationale());
            }
        }
    }

    private static List<Path> bundleDirs(Path root) {
        try (var dirs = Files.list(root)) {
            return dirs.filter(d -> Files.exists(d.resolve("manifest.json"))).sorted().toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
