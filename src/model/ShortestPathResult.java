package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortestPathResult {
    private final List<String> pathNodes;
    private final double totalCost;
    private final boolean reachable;
    private final String message;

    private ShortestPathResult(List<String> pathNodes, double totalCost, boolean reachable, String message) {
        this.pathNodes = Collections.unmodifiableList(new ArrayList<String>(pathNodes));
        this.totalCost = totalCost;
        this.reachable = reachable;
        this.message = message;
    }

    public static ShortestPathResult success(List<String> pathNodes, double totalCost) {
        return new ShortestPathResult(pathNodes, totalCost, true, "OK");
    }

    public static ShortestPathResult unreachable(String message) {
        return new ShortestPathResult(Collections.<String>emptyList(), Double.POSITIVE_INFINITY, false, message);
    }

    public List<String> getPathNodes() {
        return pathNodes;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isReachable() {
        return reachable;
    }

    public String getMessage() {
        return message;
    }
}
