package model;

public class Edge {
    private final String fromLocation;
    private final String toLocation;
    private final double weight;

    public Edge(String fromLocation, String toLocation, double weight) {
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("fromLocation must not be empty");
        }
        if (toLocation == null || toLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("toLocation must not be empty");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra requires non-negative edge weights");
        }
        this.fromLocation = fromLocation.trim();
        this.toLocation = toLocation.trim();
        this.weight = weight;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public double getWeight() {
        return weight;
    }
}
