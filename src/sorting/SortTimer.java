package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.LocationCandidate;
import model.SortingResult;

public class SortTimer {
    private final TopKSelector topKSelector;

    public SortTimer(TopKSelector topKSelector) {
        this.topKSelector = topKSelector;
    }

    public SortingResult measure(
            String datasetName,
            List<LocationCandidate> originalCandidates,
            SortingAlgorithm algorithm,
            int rounds,
            int warmupRounds,
            int topK) {
        for (int warmup = 0; warmup < warmupRounds; warmup++) {
            List<LocationCandidate> warmupCopy = new ArrayList<LocationCandidate>(originalCandidates);
            algorithm.sort(warmupCopy);
            validateSorted(warmupCopy, algorithm.getName(), datasetName);
        }

        long[] durations = new long[rounds];
        List<LocationCandidate> lastSortedCopy = new ArrayList<LocationCandidate>();

        for (int round = 0; round < rounds; round++) {
            List<LocationCandidate> workingCopy = new ArrayList<LocationCandidate>(originalCandidates);

            long startTime = System.nanoTime();
            algorithm.sort(workingCopy);
            long endTime = System.nanoTime();

            validateSorted(workingCopy, algorithm.getName(), datasetName);
            durations[round] = endTime - startTime;
            lastSortedCopy = workingCopy;
        }

        double meanRuntime = calculateMean(durations);
        double standardDeviation = calculateStandardDeviation(durations, meanRuntime);
        double medianRuntime = calculateMedian(durations);
        long minRuntime = findMin(durations);
        long maxRuntime = findMax(durations);
        double coefficientOfVariation = meanRuntime == 0.0 ? 0.0 : standardDeviation / meanRuntime;
        List<LocationCandidate> topCandidates = topKSelector.selectTop(lastSortedCopy, topK);

        return new SortingResult(
                datasetName,
                algorithm.getName(),
                meanRuntime,
                standardDeviation,
                medianRuntime,
                minRuntime,
                maxRuntime,
                coefficientOfVariation,
                rounds,
                warmupRounds,
                lastSortedCopy,
                topCandidates);
    }

    public boolean haveSameOrder(List<SortingResult> results) {
        if (results.size() <= 1) {
            return true;
        }

        List<LocationCandidate> expected = results.get(0).getSortedCandidates();
        for (int index = 1; index < results.size(); index++) {
            if (!expected.equals(results.get(index).getSortedCandidates())) {
                return false;
            }
        }
        return true;
    }

    private void validateSorted(List<LocationCandidate> candidates, String algorithmName, String datasetName) {
        for (int index = 1; index < candidates.size(); index++) {
            if (candidates.get(index - 1).compareTo(candidates.get(index)) > 0) {
                throw new IllegalStateException(
                        algorithmName + " produced invalid order for " + datasetName + " at index " + index);
            }
        }
    }

    private double calculateMean(long[] durations) {
        long total = 0L;
        for (long duration : durations) {
            total += duration;
        }
        return total / (double) durations.length;
    }

    private double calculateStandardDeviation(long[] durations, double mean) {
        double squaredDifferenceTotal = 0.0;
        for (long duration : durations) {
            double difference = duration - mean;
            squaredDifferenceTotal += difference * difference;
        }
        return Math.sqrt(squaredDifferenceTotal / durations.length);
    }

    private double calculateMedian(long[] durations) {
        long[] sortedDurations = Arrays.copyOf(durations, durations.length);
        Arrays.sort(sortedDurations);

        int middle = sortedDurations.length / 2;
        if (sortedDurations.length % 2 == 1) {
            return sortedDurations[middle];
        }
        return (sortedDurations[middle - 1] + sortedDurations[middle]) / 2.0;
    }

    private long findMin(long[] durations) {
        long min = durations[0];
        for (long duration : durations) {
            if (duration < min) {
                min = duration;
            }
        }
        return min;
    }

    private long findMax(long[] durations) {
        long max = durations[0];
        for (long duration : durations) {
            if (duration > max) {
                max = duration;
            }
        }
        return max;
    }
}
