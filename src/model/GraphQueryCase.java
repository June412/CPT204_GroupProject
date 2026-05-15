package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphQueryCase {
    private final String caseName;
    private final String startLocation;
    private final String destinationLocation;
    private final List<String> waypointLocations;

    public GraphQueryCase(
            String caseName,
            String startLocation,
            String destinationLocation,
            List<String> waypointLocations) {
        if (caseName == null || caseName.trim().isEmpty()) {
            throw new IllegalArgumentException("caseName must not be empty");
        }
        if (startLocation == null || startLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("startLocation must not be empty");
        }
        if (destinationLocation == null || destinationLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("destinationLocation must not be empty");
        }
        this.caseName = caseName.trim();
        this.startLocation = startLocation.trim();
        this.destinationLocation = destinationLocation.trim();
        if (waypointLocations == null) {
            this.waypointLocations = Collections.emptyList();
        } else {
            // Copy waypoints so the case cannot change later by accident
            this.waypointLocations = Collections.unmodifiableList(new ArrayList<String>(waypointLocations));
        }
    }

    public String getCaseName() {
        return caseName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public List<String> getWaypointLocations() {
        return waypointLocations;
    }

    public List<String> getOrderedStops() {
        List<String> stops = new ArrayList<String>();
        // The solver links each neighboring pair in this list
        stops.add(startLocation);
        stops.addAll(waypointLocations);
        stops.add(destinationLocation);
        return stops;
    }
}
