package sorting;

import java.util.List;

import model.LocationCandidate;

public class BubbleSort extends AbstractSortingAlgorithm {
    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public void sort(List<LocationCandidate> candidates) {
        int size = candidates.size();
        boolean swapped = true;

        // Stop early if a full pass did not swap anything
        for (int pass = 0; pass < size - 1 && swapped; pass++) {
            swapped = false;
            for (int index = 0; index < size - pass - 1; index++) {
                if (compare(candidates.get(index), candidates.get(index + 1)) > 0) {
                    swap(candidates, index, index + 1);
                    swapped = true;
                }
            }
        }
    }
}
