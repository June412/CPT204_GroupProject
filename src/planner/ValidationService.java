package planner;

import graph.WeightedGraph;
import model.Edge;
import model.GraphQueryCase;
import model.LocationCandidate;
import model.ShortestPathResult;
import model.SortingResult;
import model.ValidationCheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ValidationService {
    private static final double COST_TOLERANCE = 0.000001;

    public List<ValidationCheck> validate(
            Map<String, List<LocationCandidate>> datasets,
            Map<String, List<SortingResult>> sortingResultsByDataset,
            Map<String, Boolean> sameOrderByDataset,
            Map<String, List<LocationCandidate>> selectedTargetsByDataset,
            WeightedGraph graph,
            List<GraphQueryCase> queryCases,
            Map<String, ShortestPathResult> shortestPathResults,
            int expectedCandidateCount,
            int expectedGraphNodeCount,
            int expectedGraphEdgeCount) {
        List<ValidationCheck> checks = new ArrayList<ValidationCheck>();
        validateDatasets(checks, datasets, expectedCandidateCount);
        validateSortingResults(checks, sortingResultsByDataset, sameOrderByDataset);
        validateSelectedTargets(checks, selectedTargetsByDataset, graph);
        validateGraph(checks, graph, expectedGraphNodeCount, expectedGraphEdgeCount);
        validateShortestPathResults(checks, graph, queryCases, shortestPathResults);
        return checks;
    }

    private void validateDatasets(
            List<ValidationCheck> checks,
            Map<String, List<LocationCandidate>> datasets,
            int expectedCandidateCount) {
        for (Map.Entry<String, List<LocationCandidate>> entry : datasets.entrySet()) {
            List<LocationCandidate> candidates = entry.getValue();
            checks.add(new ValidationCheck(
                    entry.getKey() + " row count",
                    candidates.size() == expectedCandidateCount,
                    "actual=" + candidates.size() + ", expected=" + expectedCandidateCount));

            Set<String> locationIds = new HashSet<String>();
            boolean allUnique = true;
            for (LocationCandidate candidate : candidates) {
                if (!locationIds.add(candidate.getLocationId())) {
                    allUnique = false;
                    break;
                }
            }
            checks.add(new ValidationCheck(
                    entry.getKey() + " unique location_id",
                    allUnique,
                    "unique=" + locationIds.size() + ", rows=" + candidates.size()));
        }
    }

    private void validateSortingResults(
            List<ValidationCheck> checks,
            Map<String, List<SortingResult>> sortingResultsByDataset,
            Map<String, Boolean> sameOrderByDataset) {
        for (Map.Entry<String, List<SortingResult>> entry : sortingResultsByDataset.entrySet()) {
            boolean allAlgorithmsPresent = entry.getValue().size() == 3;
            checks.add(new ValidationCheck(
                    entry.getKey() + " sorting algorithms executed",
                    allAlgorithmsPresent,
                    "executed=" + entry.getValue().size() + ", expected=3"));

            Boolean sameOrder = sameOrderByDataset.get(entry.getKey());
            checks.add(new ValidationCheck(
                    entry.getKey() + " same sorted order",
                    Boolean.TRUE.equals(sameOrder),
                    "Bubble, Quick, and Merge outputs are compared"));
        }
    }

    private void validateSelectedTargets(
            List<ValidationCheck> checks,
            Map<String, List<LocationCandidate>> selectedTargetsByDataset,
            WeightedGraph graph) {
        for (Map.Entry<String, List<LocationCandidate>> entry : selectedTargetsByDataset.entrySet()) {
            List<LocationCandidate> selectedTargets = entry.getValue();
            checks.add(new ValidationCheck(
                    entry.getKey() + " selected target count",
                    selectedTargets.size() == 10,
                    "selected=" + selectedTargets.size() + ", expected=10"));

            boolean allInGraph = true;
            for (LocationCandidate candidate : selectedTargets) {
                if (!graph.containsNode(candidate.getLocationId())) {
                    allInGraph = false;
                    break;
                }
            }
            checks.add(new ValidationCheck(
                    entry.getKey() + " selected targets exist in graph",
                    allInGraph,
                    "all selected targets must be query nodes in paths.csv"));
        }
    }

    private void validateGraph(
            List<ValidationCheck> checks,
            WeightedGraph graph,
            int expectedGraphNodeCount,
            int expectedGraphEdgeCount) {
        checks.add(new ValidationCheck(
                "Graph node count",
                graph.getNodeCount() == expectedGraphNodeCount,
                "actual=" + graph.getNodeCount() + ", expected=" + expectedGraphNodeCount));
        checks.add(new ValidationCheck(
                "Graph undirected edge count",
                graph.getUndirectedEdgeCount() == expectedGraphEdgeCount,
                "actual=" + graph.getUndirectedEdgeCount() + ", expected=" + expectedGraphEdgeCount));
        checks.add(new ValidationCheck(
                "Graph edge weights are non-negative",
                graph.hasOnlyNonNegativeWeights(),
                "min=" + formatCost(graph.getMinimumEdgeWeight())
                        + ", max=" + formatCost(graph.getMaximumEdgeWeight())));

        boolean allEdgesHaveReverse = true;
        for (String node : graph.getNodes()) {
            for (Edge edge : graph.getEdgesFrom(node)) {
                double reverseWeight = graph.getEdgeWeight(edge.getToLocation(), edge.getFromLocation());
                if (Double.isInfinite(reverseWeight)
                        || Math.abs(reverseWeight - edge.getWeight()) > COST_TOLERANCE) {
                    allEdgesHaveReverse = false;
                    break;
                }
            }
            if (!allEdgesHaveReverse) {
                break;
            }
        }
        checks.add(new ValidationCheck(
                "Graph edges are bidirectional",
                allEdgesHaveReverse,
                "each from->to edge has matching to->from edge"));
    }

    private void validateShortestPathResults(
            List<ValidationCheck> checks,
            WeightedGraph graph,
            List<GraphQueryCase> queryCases,
            Map<String, ShortestPathResult> shortestPathResults) {
        for (GraphQueryCase queryCase : queryCases) {
            ShortestPathResult result = shortestPathResults.get(queryCase.getCaseName());
            checks.add(new ValidationCheck(
                    queryCase.getCaseName() + " reachable",
                    result != null && result.isReachable(),
                    result == null ? "missing result" : result.getMessage()));

            if (result == null || !result.isReachable()) {
                continue;
            }

            checks.add(new ValidationCheck(
                    queryCase.getCaseName() + " start/destination",
                    hasExpectedEndpoints(queryCase, result),
                    "path starts at " + queryCase.getStartLocation()
                            + " and ends at " + queryCase.getDestinationLocation()));

            checks.add(new ValidationCheck(
                    queryCase.getCaseName() + " waypoint order",
                    containsWaypointsInOrder(queryCase, result),
                    "required waypoints=" + queryCase.getWaypointLocations()));

            double recalculatedCost = calculatePathCost(graph, result.getPathNodes());
            boolean costMatches = !Double.isInfinite(recalculatedCost)
                    && Math.abs(recalculatedCost - result.getTotalCost()) <= COST_TOLERANCE;
            checks.add(new ValidationCheck(
                    queryCase.getCaseName() + " path cost matches graph",
                    costMatches,
                    "reported=" + formatCost(result.getTotalCost())
                            + ", recalculated=" + formatCost(recalculatedCost)));

            if (queryCase.getStartLocation().equals(queryCase.getDestinationLocation())) {
                boolean selfPathCorrect = result.getPathNodes().size() == 1
                        && Math.abs(result.getTotalCost()) <= COST_TOLERANCE;
                checks.add(new ValidationCheck(
                        queryCase.getCaseName() + " self path",
                        selfPathCorrect,
                        "self query should return one node and cost 0"));
            }
        }
    }

    private boolean hasExpectedEndpoints(GraphQueryCase queryCase, ShortestPathResult result) {
        List<String> pathNodes = result.getPathNodes();
        if (pathNodes.isEmpty()) {
            return false;
        }
        return pathNodes.get(0).equals(queryCase.getStartLocation())
                && pathNodes.get(pathNodes.size() - 1).equals(queryCase.getDestinationLocation());
    }

    private boolean containsWaypointsInOrder(GraphQueryCase queryCase, ShortestPathResult result) {
        int searchStart = 0;
        for (String waypoint : queryCase.getWaypointLocations()) {
            int foundIndex = findNodeAtOrAfter(result.getPathNodes(), waypoint, searchStart);
            if (foundIndex < 0) {
                return false;
            }
            searchStart = foundIndex + 1;
        }
        return true;
    }

    private int findNodeAtOrAfter(List<String> pathNodes, String targetNode, int startIndex) {
        for (int index = startIndex; index < pathNodes.size(); index++) {
            if (pathNodes.get(index).equals(targetNode)) {
                return index;
            }
        }
        return -1;
    }

    private double calculatePathCost(WeightedGraph graph, List<String> pathNodes) {
        double total = 0.0;
        for (int index = 0; index < pathNodes.size() - 1; index++) {
            double edgeWeight = graph.getEdgeWeight(pathNodes.get(index), pathNodes.get(index + 1));
            if (Double.isInfinite(edgeWeight)) {
                return Double.POSITIVE_INFINITY;
            }
            total += edgeWeight;
        }
        return total;
    }

    private String formatCost(double cost) {
        if (Double.isInfinite(cost)) {
            return "Infinity";
        }
        // Keep cost formatting the same on different computers
        if (cost == Math.rint(cost)) {
            return String.format(Locale.US, "%.0f", cost);
        }
        return String.format(Locale.US, "%.3f", cost);
    }
}
