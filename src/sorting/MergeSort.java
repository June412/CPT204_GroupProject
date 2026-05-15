package sorting;

import java.util.ArrayList;
import java.util.List;

import model.LocationCandidate;

public class MergeSort extends AbstractSortingAlgorithm {
    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public void sort(List<LocationCandidate> candidates) {
        if (candidates.size() <= 1) {
            return;
        }
        // Temp list so merge can read old values while writing back
        List<LocationCandidate> temporary = new ArrayList<LocationCandidate>(candidates);
        mergeSort(candidates, temporary, 0, candidates.size() - 1);
    }

    private void mergeSort(List<LocationCandidate> candidates, List<LocationCandidate> temporary, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;
        mergeSort(candidates, temporary, left, middle);
        mergeSort(candidates, temporary, middle + 1, right);
        merge(candidates, temporary, left, middle, right);
    }

    private void merge(
            List<LocationCandidate> candidates,
            List<LocationCandidate> temporary,
            int left,
            int middle,
            int right) {
        for (int index = left; index <= right; index++) {
            temporary.set(index, candidates.get(index));
        }

        int leftIndex = left;
        int rightIndex = middle + 1;
        int targetIndex = left;

        while (leftIndex <= middle && rightIndex <= right) {
            if (compare(temporary.get(leftIndex), temporary.get(rightIndex)) <= 0) {
                candidates.set(targetIndex, temporary.get(leftIndex));
                leftIndex++;
            } else {
                candidates.set(targetIndex, temporary.get(rightIndex));
                rightIndex++;
            }
            targetIndex++;
        }

        while (leftIndex <= middle) {
            candidates.set(targetIndex, temporary.get(leftIndex));
            leftIndex++;
            targetIndex++;
        }
    }
}
