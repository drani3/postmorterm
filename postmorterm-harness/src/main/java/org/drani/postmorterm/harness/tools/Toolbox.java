package org.drani.postmorterm.harness.tools;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.drani.postmorterm.bundle.BundleManifest;
import org.drani.postmorterm.bundle.DeployEvent;
import org.drani.postmorterm.bundle.EvidenceBundle;
import org.drani.postmorterm.bundle.LogLine;
import org.drani.postmorterm.bundle.MetricPoint;
import org.drani.postmorterm.bundle.Span;

/**
 * The only view of an incident an agent gets. Every call is counted so runs can be compared on investigation
 * cost as well as accuracy. Ground truth is deliberately unreachable from here.
 */
public class Toolbox {

    private final EvidenceBundle bundle;
    private final Map<String, Integer> calls = new TreeMap<>();

    public Toolbox(EvidenceBundle bundle) {
        this.bundle = bundle;
    }

    public BundleManifest manifest() {
        return bundle.manifest();
    }

    /** Null arguments match anything. */
    public List<LogLine> searchLogs(String service, String level, String contains) {
        record("search_logs");
        return bundle.logs().stream()
                .filter(l -> service == null || service.equals(l.service()))
                .filter(l -> level == null || level.equalsIgnoreCase(l.level()))
                .filter(l -> contains == null || l.message().contains(contains))
                .toList();
    }

    public Map<String, Long> countLogsByService(String level) {
        record("count_logs");
        return bundle.logs().stream()
                .filter(l -> level == null || level.equalsIgnoreCase(l.level()))
                .collect(Collectors.groupingBy(LogLine::service, TreeMap::new, Collectors.counting()));
    }

    public List<Span> getTrace(String traceId) {
        record("get_trace");
        return bundle.spans().stream().filter(s -> s.traceId().equals(traceId)).toList();
    }

    public List<MetricPoint> queryMetric(String service, String name) {
        record("query_metric");
        return bundle.metrics().stream()
                .filter(m -> service == null || service.equals(m.service()))
                .filter(m -> m.name().equals(name))
                .toList();
    }

    public List<String> listMetricNames() {
        record("list_metrics");
        return bundle.metrics().stream().map(MetricPoint::name).distinct().sorted().toList();
    }

    public List<DeployEvent> listDeploys() {
        record("list_deploys");
        return bundle.deploys();
    }

    public Map<String, Integer> callCounts() {
        return Map.copyOf(calls);
    }

    public int totalCalls() {
        return calls.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void record(String tool) {
        calls.merge(tool, 1, Integer::sum);
    }
}
