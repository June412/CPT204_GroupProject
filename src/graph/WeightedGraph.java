package graph;

import model.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WeightedGraph {
    private final Map<String, List<Edge>> adjacencyList;
    private int undirectedEdgeCount;

    public WeightedGraph() {
        this.adjacencyList = new LinkedHashMap<String, List<Edge>>();
        this.undirectedEdgeCount = 0;
    }

    public void addUndirectedEdge(String fromLocation, String toLocation, double weight) {
        addDirectedEdge(fromLocation, toLocation, weight);
        addDirectedEdge(toLocation, fromLocation, weight);
        undirectedEdgeCount++;
    }

    public List<Edge> getEdgesFrom(String location) {
        List<Edge> edges = adjacencyList.get(location);
        if (edges == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(edges);
    }

    public double getEdgeWeight(String fromLocation, String toLocation) {
        List<Edge> edges = adjacencyList.get(fromLocation);
        if (edges == null) {
            return Double.POSITIVE_INFINITY;
        }

        double bestWeight = Double.POSITIVE_INFINITY;
        for (Edge edge : edges) {
            // If the CSV has repeated edges, use the cheapest one
            if (edge.getToLocation().equals(toLocation) && edge.getWeight() < bestWeight) {
                bestWeight = edge.getWeight();
            }
        }
        return bestWeight;
    }

    public boolean containsNode(String location) {
        return adjacencyList.containsKey(location);
    }

    public int getNodeCount() {
        return adjacencyList.size();
    }

    public int getUndirectedEdgeCount() {
        return undirectedEdgeCount;
    }

    public double getMinimumEdgeWeight() {
        double minimum = Double.POSITIVE_INFINITY;
        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge edge : edges) {
                if (edge.getWeight() < minimum) {
                    minimum = edge.getWeight();
                }
            }
        }
        return minimum;
    }

    public double getMaximumEdgeWeight() {
        double maximum = Double.NEGATIVE_INFINITY;
        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge edge : edges) {
                if (edge.getWeight() > maximum) {
                    maximum = edge.getWeight();
                }
            }
        }
        return maximum;
    }

    public boolean hasOnlyNonNegativeWeights() {
        for (List<Edge> edges : adjacencyList.values()) {
            for (Edge edge : edges) {
                if (edge.getWeight() < 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Set<String> getNodes() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    private void addDirectedEdge(String fromLocation, String toLocation, double weight) {
        if (!adjacencyList.containsKey(fromLocation)) {
            adjacencyList.put(fromLocation, new ArrayList<Edge>());
        }
        if (!adjacencyList.containsKey(toLocation)) {
            adjacencyList.put(toLocation, new ArrayList<Edge>());
        }
        adjacencyList.get(fromLocation).add(new Edge(fromLocation, toLocation, weight));
    }
}
