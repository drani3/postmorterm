package org.drani.postmorterm.bundle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import tools.jackson.databind.json.JsonMapper;

/**
 * Reads bundles laid out as a directory:
 * {@code manifest.json, logs.jsonl, spans.jsonl, metrics.jsonl, deploys.jsonl, ground_truth.json}.
 */
public class BundleStore {

    private final JsonMapper json;

    public BundleStore(JsonMapper json) {
        this.json = json;
    }

    public EvidenceBundle load(Path dir) {
        return new EvidenceBundle(
                readJson(dir.resolve("manifest.json"), BundleManifest.class),
                readJsonl(dir.resolve("logs.jsonl"), LogLine.class),
                readJsonl(dir.resolve("spans.jsonl"), Span.class),
                readJsonl(dir.resolve("metrics.jsonl"), MetricPoint.class),
                readJsonl(dir.resolve("deploys.jsonl"), DeployEvent.class));
    }

    public GroundTruth groundTruth(Path dir) {
        return readJson(dir.resolve("ground_truth.json"), GroundTruth.class);
    }

    private <T> T readJson(Path file, Class<T> type) {
        return json.readValue(file.toFile(), type);
    }

    private <T> List<T> readJsonl(Path file, Class<T> type) {
        if (!Files.exists(file)) {
            return List.of();
        }
        try (var lines = Files.lines(file)) {
            return lines.filter(l -> !l.isBlank()).map(l -> json.readValue(l, type)).toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
