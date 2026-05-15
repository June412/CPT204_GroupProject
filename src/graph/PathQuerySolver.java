package graph;

import model.GraphQueryCase;
import model.ShortestPathResult;

import java.util.ArrayList;
import java.util.List;

public class PathQuerySolver {
    private final DijkstraSolver dijkstraSolver;

    public PathQuerySolver(DijkstraSolver dijkstraSolver) {
        this.dijkstraSolver = dijkstraSolver;
    }

    public ShortestPathResult solve(WeightedGraph graph, GraphQueryCase queryCase) {
        List<String> orderedStops = queryCase.getOrderedStops();
        List<String> completePath = new ArrayList<String>();
        double totalCost = 0.0;

        for (int index = 0; index < orderedStops.size() - 1; index++) {
            String segmentStart = orderedStops.get(index);
            String segmentDestination = orderedStops.get(index + 1);

            ShortestPathResult segmentResult = dijkstraSolver.findShortestPath(
                    graph,
                    segmentStart,
                    segmentDestination);

            if (!segmentResult.isReachable()) {
                return ShortestPathResult.unreachable(
                        queryCase.getCaseName()
                                + " failed between "
                                + segmentStart
                                + " and "
                                + segmentDestination
                                + ": "
                                + segmentResult.getMessage());
            }

            appendSegment(completePath, segmentResult.getPathNodes());
            totalCost += segmentResult.getTotalCost();
        }

        return ShortestPathResult.success(completePath, totalCost);
    }

    private void appendSegment(List<String> completePath, List<String> segmentPath) {
        if (segmentPath.isEmpty()) {
            return;
        }
        if (completePath.isEmpty()) {
            completePath.addAll(segmentPath);
            return;
        }
        // Skip the first node so the waypoint is not added twice
        for (int index = 1; index < segmentPath.size(); index++) {
            completePath.add(segmentPath.get(index));
        }
    }
}
