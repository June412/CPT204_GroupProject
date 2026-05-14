package model;

public class PathMetrics {
    private final String caseName;
    private final double totalCost;
    private final int nodeCount;
    private final int edgeCount;
    private final int waypointCount;

    public PathMetrics(
            String caseName,
            double totalCost,
            int nodeCount,
            int edgeCount,
            int waypointCount) {
        this.caseName = caseName;
        this.totalCost = totalCost;
        this.nodeCount = nodeCount;
        this.edgeCount = edgeCount;
        this.waypointCount = waypointCount;
    }

    public String getCaseName() {
        return caseName;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getWaypointCount() {
        return waypointCount;
    }
}
