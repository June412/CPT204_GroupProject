package planner;

import graph.WeightedGraph;
import model.AlgorithmTradeoff;
import model.DatasetProfile;
import model.GraphQueryCase;
import model.LocationCandidate;
import model.PathMetrics;
import model.ShortestPathResult;
import model.SortingResult;
import model.ValidationCheck;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OutputFormatter {
    public void printHeader(String dataDirectory, int measurementRounds, int warmupRounds) {
        printDivider();
        System.out.println("Urban Infrastructure Inspection System");
        System.out.println("Java version: " + System.getProperty("java.version", "Unknown"));
        System.out.println("JVM: " + System.getProperty("java.vm.name", "Unknown"));
        System.out.println("OS: " + System.getProperty("os.name", "Unknown")
                + " " + System.getProperty("os.version", ""));
        System.out.println("Data directory: " + dataDirectory);
        System.out.println("Measurement rounds per sorting algorithm: " + measurementRounds);
        System.out.println("Warm-up rounds per sorting algorithm: " + warmupRounds);
        printDivider();
    }

    public void printDatasetSummary(Map<String, List<LocationCandidate>> datasets, WeightedGraph graph) {
        System.out.println();
        System.out.println("DATA SUMMARY");
        for (Map.Entry<String, List<LocationCandidate>> entry : datasets.entrySet()) {
            System.out.printf("%-10s candidates: %d%n", entry.getKey(), entry.getValue().size());
        }
        System.out.printf("Graph nodes: %d%n", graph.getNodeCount());
        System.out.printf("Graph undirected edges: %d%n", graph.getUndirectedEdgeCount());
    }

    public void printDatasetProfiles(List<DatasetProfile> datasetProfiles) {
        System.out.println();
        System.out.println("TASK A - DATASET PROFILE");
        System.out.printf(
                "%-10s | %-6s | %-13s | %-20s | %-18s | %-18s | %-24s%n",
                "Dataset",
                "Rows",
                "Unique Scores",
                "Duplicate Score Rows",
                "Adjacent OK (%)",
                "Adjacent Wrong",
                "Input Order Note");
        printDivider();
        for (DatasetProfile profile : datasetProfiles) {
            System.out.printf(
                    "%-10s | %-6d | %-13d | %-20d | %-18s | %-18s | %-24s%n",
                    profile.getDatasetName(),
                    profile.getRowCount(),
                    profile.getUniquePriorityScoreCount(),
                    profile.getDuplicateScoreRowCount(),
                    formatPercentage(profile.getAdjacentCorrectOrderPercentage()),
                    profile.getAdjacentOutOfOrderPairCount()
                            + " (" + formatPercentage(profile.getAdjacentOutOfOrderPercentage()) + ")",
                    profile.getInputOrderDescription());
        }
        System.out.println("Adjacent OK/Wrong uses the required ranking rule, so it estimates how ordered the raw input is before sorting.");
    }

    public void printSortingRuntimeSummary(
            Map<String, List<SortingResult>> sortingResultsByDataset,
            Map<String, Boolean> sameOrderByDataset) {
        System.out.println();
        System.out.println("TASK A - SORTING RUNTIME SUMMARY (MEAN +/- STANDARD DEVIATION)");
        System.out.printf(
                "%-10s | %-28s | %-28s | %-28s | %-11s%n",
                "Dataset",
                "Bubble",
                "Quick",
                "Merge",
                "Same Order");
        printDivider();

        for (Map.Entry<String, List<SortingResult>> entry : sortingResultsByDataset.entrySet()) {
            List<SortingResult> results = entry.getValue();
            System.out.printf(
                    "%-10s | %-28s | %-28s | %-28s | %-11s%n",
                    entry.getKey(),
                    formatMeanAndStandardDeviation(findResult(results, "Bubble Sort")),
                    formatMeanAndStandardDeviation(findResult(results, "Quick Sort")),
                    formatMeanAndStandardDeviation(findResult(results, "Merge Sort")),
                    sameOrderByDataset.get(entry.getKey()) ? "Yes" : "No");
        }

        System.out.println();
        System.out.println("TASK A - SORTING RUNTIME DETAILS");
        System.out.printf(
                "%-10s | %-12s | %-6s | %-6s | %-10s | %-10s | %-10s | %-10s | %-10s | %-8s%n",
                "Dataset",
                "Algorithm",
                "Warmup",
                "Runs",
                "Mean(ms)",
                "Median(ms)",
                "StdDev(ms)",
                "Min(ms)",
                "Max(ms)",
                "CV");
        printDivider();
        for (Map.Entry<String, List<SortingResult>> entry : sortingResultsByDataset.entrySet()) {
            for (SortingResult result : entry.getValue()) {
                System.out.printf(
                        "%-10s | %-12s | %-6d | %-6d | %-10s | %-10s | %-10s | %-10s | %-10s | %-8s%n",
                        entry.getKey(),
                        shortAlgorithmName(result.getAlgorithmName()),
                        result.getWarmupRounds(),
                        result.getMeasurementRounds(),
                        formatMilliseconds(result.getMeanRuntimeNanos()),
                        formatMilliseconds(result.getMedianRuntimeNanos()),
                        formatMilliseconds(result.getStandardDeviationRuntimeNanos()),
                        formatMilliseconds(result.getMinRuntimeNanos()),
                        formatMilliseconds(result.getMaxRuntimeNanos()),
                        formatCoefficientOfVariation(result.getCoefficientOfVariation()));
            }
        }
    }

    public void printAlgorithmDecisionSupport(List<AlgorithmTradeoff> algorithmTradeoffs) {
        System.out.println();
        System.out.println("TASK A - TIME AND STABILITY DECISION SUPPORT");
        System.out.println("Avg Run CV measures repeated-run timing noise; Cross-Data CV/Range compares mean runtime across Dataset A-C.");
        System.out.printf(
                "%-12s | %-12s | %-10s | %-13s | %-14s%n",
                "Algorithm",
                "Avg Mean(ms)",
                "Avg Run CV",
                "Cross-Data CV",
                "Range(ms)");
        printDivider();
        for (AlgorithmTradeoff tradeoff : algorithmTradeoffs) {
            System.out.printf(
                    "%-12s | %-12s | %-10s | %-13s | %-14s%n",
                    tradeoff.getAlgorithmName(),
                    formatPlainMilliseconds(tradeoff.getAverageMeanRuntimeMillis()),
                    formatCoefficientOfVariation(tradeoff.getAverageRunCoefficientOfVariation()),
                    formatCoefficientOfVariation(tradeoff.getCrossDatasetCoefficientOfVariation()),
                    formatPlainMilliseconds(tradeoff.getCrossDatasetRuntimeRangeMillis()));
        }

        System.out.println();
        System.out.println("TASK A - TIME AND SPACE DECISION SUPPORT");
        System.out.printf(
                "%-12s | %-34s | %-46s | %s%n",
                "Algorithm",
                "Time Complexity",
                "Extra Space",
                "Decision Note");
        printDivider();
        for (AlgorithmTradeoff tradeoff : algorithmTradeoffs) {
            System.out.printf(
                    "%-12s | %-34s | %-46s | %s%n",
                    tradeoff.getAlgorithmName(),
                    tradeoff.getTimeComplexity(),
                    tradeoff.getExtraSpaceComplexity(),
                    tradeoff.getDecisionNote());
        }

        System.out.println();
        System.out.println("Suggested final choice when both time and space are considered: Merge Sort.");
        System.out.println("Reason: Merge Sort has predictable O(n log n) runtime and good observed performance across the three datasets. Its O(n) extra space is acceptable for 1000 candidate records.");
        System.out.println("Memory-limited alternative: Quick Sort.");
        System.out.println("Reason: Quick Sort normally uses less extra memory than Merge Sort, but it is more sensitive to input order and has O(n^2) worst-case time.");
    }

    public void printSelectedTargets(Map<String, List<LocationCandidate>> selectedTargetsByDataset) {
        System.out.println();
        System.out.println("TASK A - TOP 10 SELECTED INSPECTION TARGETS (A1-A10, B1-B10, C1-C10)");
        System.out.println("Canonical selected lists are taken from Merge Sort after same-order validation.");
        System.out.printf(
                "%-6s | %-8s | %-18s | %-8s | %-18s | %-8s | %-18s%n",
                "Rank",
                "A Label",
                "Dataset A",
                "B Label",
                "Dataset B",
                "C Label",
                "Dataset C");
        printDivider();

        List<LocationCandidate> datasetA = selectedTargetsByDataset.get("Dataset A");
        List<LocationCandidate> datasetB = selectedTargetsByDataset.get("Dataset B");
        List<LocationCandidate> datasetC = selectedTargetsByDataset.get("Dataset C");

        int rowCount = Math.min(datasetA.size(), Math.min(datasetB.size(), datasetC.size()));
        for (int index = 0; index < rowCount; index++) {
            System.out.printf(
                    "%-6d | %-8s | %-18s | %-8s | %-18s | %-8s | %-18s%n",
                    index + 1,
                    "A" + (index + 1),
                    datasetA.get(index),
                    "B" + (index + 1),
                    datasetB.get(index),
                    "C" + (index + 1),
                    datasetC.get(index));
        }
    }

    public void printGraphWeightSummary(WeightedGraph graph) {
        System.out.println();
        System.out.println("TASK B - GRAPH WEIGHT SUMMARY");
        System.out.printf(
                "Minimum edge weight: %s; Maximum edge weight: %s; Non-negative for Dijkstra: %s%n",
                formatCost(graph.getMinimumEdgeWeight()),
                formatCost(graph.getMaximumEdgeWeight()),
                graph.hasOnlyNonNegativeWeights() ? "Yes" : "No");
    }

    public void printGraphResults(
            List<GraphQueryCase> queryCases,
            Map<String, ShortestPathResult> shortestPathResults) {
        System.out.println();
        System.out.println("TASK B - SHORTEST PATH RESULTS");

        for (GraphQueryCase queryCase : queryCases) {
            ShortestPathResult result = shortestPathResults.get(queryCase.getCaseName());
            System.out.println();
            System.out.println(queryCase.getCaseName());
            System.out.println("Start: " + queryCase.getStartLocation());
            System.out.println("Waypoints: " + formatWaypoints(queryCase.getWaypointLocations()));
            System.out.println("Destination: " + queryCase.getDestinationLocation());
            System.out.println("Status: " + result.getMessage());
            System.out.println("Total cost: " + formatCost(result.getTotalCost()));
            System.out.println("Path: " + formatPath(result));
        }
    }

    public void printPathMetrics(List<PathMetrics> pathMetrics) {
        System.out.println();
        System.out.println("TASK B - PATH METRICS");
        System.out.printf(
                "%-42s | %-10s | %-13s | %-13s | %-14s%n",
                "Case",
                "Total Cost",
                "Nodes in Path",
                "Edges in Path",
                "Waypoint Count");
        printDivider();
        for (PathMetrics metrics : pathMetrics) {
            System.out.printf(
                    "%-42s | %-10s | %-13d | %-13d | %-14d%n",
                    metrics.getCaseName(),
                    formatCost(metrics.getTotalCost()),
                    metrics.getNodeCount(),
                    metrics.getEdgeCount(),
                    metrics.getWaypointCount());
        }
    }

    public void printValidationSummary(List<ValidationCheck> validationChecks) {
        System.out.println();
        System.out.println("VALIDATION SUMMARY");
        System.out.printf("%-55s | %-6s | %-60s%n", "Check", "Result", "Details");
        printDivider();
        for (ValidationCheck check : validationChecks) {
            System.out.printf(
                    "%-55s | %-6s | %-60s%n",
                    check.getCheckName(),
                    check.isPassed() ? "PASS" : "FAIL",
                    shorten(check.getDetails(), 60));
        }

        int passed = 0;
        for (ValidationCheck check : validationChecks) {
            if (check.isPassed()) {
                passed++;
            }
        }
        System.out.println("Validation checks passed: " + passed + "/" + validationChecks.size());
    }

    private SortingResult findResult(List<SortingResult> results, String algorithmName) {
        for (SortingResult result : results) {
            if (result.getAlgorithmName().equals(algorithmName)) {
                return result;
            }
        }
        return null;
    }

    private String formatMeanAndStandardDeviation(SortingResult result) {
        if (result == null) {
            return "N/A";
        }
        return String.format(
                Locale.US,
                "%.3f +/- %.3f ms",
                result.getMeanRuntimeNanos() / 1_000_000.0,
                result.getStandardDeviationRuntimeNanos() / 1_000_000.0);
    }

    private String formatPercentage(double percentage) {
        return String.format(Locale.US, "%.2f%%", percentage);
    }

    private String formatCost(double cost) {
        if (Double.isInfinite(cost)) {
            return "Infinity";
        }
        if (cost == Math.rint(cost)) {
            return String.format(Locale.US, "%.0f", cost);
        }
        return String.format(Locale.US, "%.3f", cost);
    }

    private String formatWaypoints(List<String> waypoints) {
        if (waypoints.isEmpty()) {
            return "-";
        }
        return String.join(" -> ", waypoints);
    }

    private String formatPath(ShortestPathResult result) {
        if (!result.isReachable()) {
            return "-";
        }
        return String.join(" -> ", result.getPathNodes());
    }

    private String formatMilliseconds(double nanos) {
        return String.format(Locale.US, "%.3f", nanos / 1_000_000.0);
    }

    private String formatPlainMilliseconds(double milliseconds) {
        return String.format(Locale.US, "%.3f", milliseconds);
    }

    private String formatCoefficientOfVariation(double coefficientOfVariation) {
        return String.format(Locale.US, "%.4f", coefficientOfVariation);
    }

    private String shortAlgorithmName(String algorithmName) {
        if (algorithmName.endsWith(" Sort")) {
            return algorithmName.substring(0, algorithmName.length() - 5);
        }
        return algorithmName;
    }

    private String shorten(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private void printDivider() {
        System.out.println("--------------------------------------------------------------------------------");
    }
}
