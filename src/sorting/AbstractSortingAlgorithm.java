package sorting;

import java.util.List;

import model.LocationCandidate;

public abstract class AbstractSortingAlgorithm implements SortingAlgorithm {
    protected int compare(LocationCandidate first, LocationCandidate second) {
        return first.compareTo(second);
    }

    protected void swap(List<LocationCandidate> candidates, int firstIndex, int secondIndex) {
        if (firstIndex == secondIndex) {
            return;
        }
        LocationCandidate temporary = candidates.get(firstIndex);
        candidates.set(firstIndex, candidates.get(secondIndex));
        candidates.set(secondIndex, temporary);
    }
}
