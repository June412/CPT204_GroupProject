package io;

import graph.WeightedGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PathCsvReader {
    public WeightedGraph readGraph(String filePath) throws IOException {
        WeightedGraph graph = new WeightedGraph();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("Invalid path row at " + filePath + ":" + rowNumber);
                }

                String fromLocation = parts[0].trim();
                String toLocation = parts[1].trim();
                double weight = parseDouble(parts[2].trim(), filePath, rowNumber);
                if (weight < 0.0 || Double.isNaN(weight) || Double.isInfinite(weight)) {
                    throw new IOException(
                            "Invalid Dijkstra edge weight at " + filePath + ":" + rowNumber
                                    + ". Weight must be finite and non-negative.");
                }
                graph.addUndirectedEdge(fromLocation, toLocation, weight);
            }
        }

        return graph;
    }

    private double parseDouble(String value, String filePath, int rowNumber) throws IOException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid weight at " + filePath + ":" + rowNumber, exception);
        }
    }
}
