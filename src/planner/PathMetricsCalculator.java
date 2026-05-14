package planner;

import model.GraphQueryCase;
import model.PathMetrics;
import model.ShortestPathResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathMetricsCalculator {
    public List<PathMetrics> calculate(
            List<GraphQueryCase> queryCases,
            Map<String, ShortestPathResult> shortestPathResults) {
        List<PathMetrics> metrics = new ArrayList<PathMetrics>();
        for (GraphQueryCase queryCase : queryCases) {
            ShortestPathResult result = shortestPathResults.get(queryCase.getCaseName());
            int nodeCount = result == null ? 0 : result.getPathNodes().size();
            int edgeCount = result != null && result.isReachable() && nodeCount > 0 ? nodeCount - 1 : 0;
            double totalCost = result == null ? Double.POSITIVE_INFINITY : result.getTotalCost();

            metrics.add(new PathMetrics(
                    queryCase.getCaseName(),
                    totalCost,
                    nodeCount,
                    edgeCount,
                    queryCase.getWaypointLocations().size()));
        }
        return metrics;
    }
}
