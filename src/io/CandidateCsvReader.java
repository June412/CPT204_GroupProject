package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.LocationCandidate;

public class CandidateCsvReader {
    public List<LocationCandidate> read(String filePath) throws IOException {
        List<LocationCandidate> candidates = new ArrayList<LocationCandidate>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 2) {
                    throw new IOException("Invalid candidate row at " + filePath + ":" + rowNumber);
                }

                String locationId = parts[0].trim();
                int priorityScore = parseInteger(parts[1].trim(), filePath, rowNumber);
                candidates.add(new LocationCandidate(locationId, priorityScore));
            }
        }

        return candidates;
    }

    private int parseInteger(String value, String filePath, int rowNumber) throws IOException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid priority_score at " + filePath + ":" + rowNumber, exception);
        }
    }
}
