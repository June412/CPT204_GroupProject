package sorting;

import java.util.List;

import model.LocationCandidate;

public class QuickSort extends AbstractSortingAlgorithm {
    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public void sort(List<LocationCandidate> candidates) {
        quickSort(candidates, 0, candidates.size() - 1);
    }

    private void quickSort(List<LocationCandidate> candidates, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(candidates, low, high);
        quickSort(candidates, low, pivotIndex - 1);
        quickSort(candidates, pivotIndex + 1, high);
    }

    private int partition(List<LocationCandidate> candidates, int low, int high) {
        // Use the last item as the pivot here
        LocationCandidate pivot = candidates.get(high);
        int smallerBoundary = low - 1;

        for (int index = low; index < high; index++) {
            if (compare(candidates.get(index), pivot) <= 0) {
                smallerBoundary++;
                swap(candidates, smallerBoundary, index);
            }
        }

        swap(candidates, smallerBoundary + 1, high);
        return smallerBoundary + 1;
    }
}
