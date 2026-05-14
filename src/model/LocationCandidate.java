package model;

import java.util.Objects;

public class LocationCandidate implements Comparable<LocationCandidate> {
    private final String locationId;
    private final int priorityScore;

    public LocationCandidate(String locationId, int priorityScore) {
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new IllegalArgumentException("locationId must not be empty");
        }
        this.locationId = locationId.trim();
        this.priorityScore = priorityScore;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    @Override
    public int compareTo(LocationCandidate other) {
        int scoreComparison = Integer.compare(other.priorityScore, this.priorityScore);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        return this.locationId.compareTo(other.locationId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LocationCandidate)) {
            return false;
        }
        LocationCandidate that = (LocationCandidate) object;
        return priorityScore == that.priorityScore && locationId.equals(that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, priorityScore);
    }

    @Override
    public String toString() {
        return locationId + "(" + priorityScore + ")";
    }
}
