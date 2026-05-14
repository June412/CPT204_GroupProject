package graph;

import model.Edge;
import model.ShortestPathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DijkstraSolver {
    public ShortestPathResult findShortestPath(WeightedGraph graph, String startLocation, String destinationLocation) {
        if (!graph.containsNode(startLocation)) {
            return ShortestPathResult.unreachable("Start node not found in graph: " + startLocation);
        }
        if (!graph.containsNode(destinationLocation)) {
            return ShortestPathResult.unreachable("Destination node not found in graph: " + destinationLocation);
        }
        if (startLocation.equals(destinationLocation)) {
            return ShortestPathResult.success(Collections.singletonList(startLocation), 0.0);
        }

        Map<String, Double> distances = new HashMap<String, Double>();
        Map<String, String> previousNodes = new HashMap<String, String>();
        Set<String> visited = new HashSet<String>();
        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<NodeDistance>(
                Comparator.comparingDouble(NodeDistance::getDistance).thenComparing(NodeDistance::getLocation));

        distances.put(startLocation, 0.0);
        priorityQueue.add(new NodeDistance(startLocation, 0.0));

        while (!priorityQueue.isEmpty()) {
            NodeDistance current = priorityQueue.poll();
            if (visited.contains(current.getLocation())) {
                continue;
            }
            visited.add(current.getLocation());

            if (current.getLocation().equals(destinationLocation)) {
                break;
            }

            for (Edge edge : graph.getEdgesFrom(current.getLocation())) {
                String neighbor = edge.getToLocation();
                if (visited.contains(neighbor)) {
                    continue;
                }

                double currentDistance = distances.get(current.getLocation());
                double candidateDistance = currentDistance + edge.getWeight();
                double knownDistance = distances.containsKey(neighbor)
                        ? distances.get(neighbor)
                        : Double.POSITIVE_INFINITY;

                if (candidateDistance < knownDistance) {
                    distances.put(neighbor, candidateDistance);
                    previousNodes.put(neighbor, current.getLocation());
                    priorityQueue.add(new NodeDistance(neighbor, candidateDistance));
                }
            }
        }

        if (!distances.containsKey(destinationLocation)) {
            return ShortestPathResult.unreachable(
                    "No path found from " + startLocation + " to " + destinationLocation);
        }

        List<String> path = rebuildPath(previousNodes, startLocation, destinationLocation);
        return ShortestPathResult.success(path, distances.get(destinationLocation));
    }

    private List<String> rebuildPath(
            Map<String, String> previousNodes,
            String startLocation,
            String destinationLocation) {
        List<String> path = new ArrayList<String>();
        String current = destinationLocation;

        while (current != null) {
            path.add(current);
            if (current.equals(startLocation)) {
                break;
            }
            current = previousNodes.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private static class NodeDistance {
        private final String location;
        private final double distance;

        NodeDistance(String location, double distance) {
            this.location = location;
            this.distance = distance;
        }

        String getLocation() {
            return location;
        }

        double getDistance() {
            return distance;
        }
    }
}
