package sorting;

import java.util.List;

import model.LocationCandidate;

public interface SortingAlgorithm {
    String getName();

    void sort(List<LocationCandidate> candidates);
}
