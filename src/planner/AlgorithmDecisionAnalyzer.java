package planner;

import model.AlgorithmTradeoff;
import model.SortingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlgorithmDecisionAnalyzer {
    public List<AlgorithmTradeoff> analyze(Map<String, List<SortingResult>> sortingResultsByDataset) {
        List<AlgorithmTradeoff> tradeoffs = new ArrayList<AlgorithmTradeoff>();
        tradeoffs.add(createTradeoff(
                "Bubble Sort",
                sortingResultsByDataset,
                "O(n^2)",
                "O(1)",
                "Lowest extra memory, but inefficient for general use because runtime grows quadratically."));
        tradeoffs.add(createTradeoff(
                "Quick Sort",
                sortingResultsByDataset,
                "Average O(n log n), Worst O(n^2)",
                "Average O(log n) recursion stack, Worst O(n)",
                "Good time-space balance, but the current simple pivot implementation is input-sensitive."));
        tradeoffs.add(createTradeoff(
                "Merge Sort",
                sortingResultsByDataset,
                "O(n log n)",
                "O(n)",
                "Predictable runtime and strong overall performance, but needs extra memory."));
        return tradeoffs;
    }

    private AlgorithmTradeoff createTradeoff(
            String algorithmName,
            Map<String, List<SortingResult>> sortingResultsByDataset,
            String timeComplexity,
            String extraSpaceComplexity,
            String decisionNote) {
        double totalMeanRuntimeMillis = 0.0;
        double totalRunCoefficientOfVariation = 0.0;
        List<Double> datasetMeanRuntimeMillis = new ArrayList<Double>();
        int resultCount = 0;

        for (List<SortingResult> results : sortingResultsByDataset.values()) {
            SortingResult result = findResultByAlgorithm(results, algorithmName);
            if (result != null) {
                double meanRuntimeMillis = result.getMeanRuntimeNanos() / 1_000_000.0;
                totalMeanRuntimeMillis += meanRuntimeMillis;
                totalRunCoefficientOfVariation += result.getCoefficientOfVariation();
                datasetMeanRuntimeMillis.add(meanRuntimeMillis);
                resultCount++;
            }
        }

        double averageMeanRuntimeMillis = resultCount == 0 ? 0.0 : totalMeanRuntimeMillis / resultCount;
        double averageRunCoefficientOfVariation = resultCount == 0 ? 0.0 : totalRunCoefficientOfVariation / resultCount;
        double crossDatasetStandardDeviation = calculateStandardDeviation(
                datasetMeanRuntimeMillis,
                averageMeanRuntimeMillis);
        double crossDatasetCoefficientOfVariation = averageMeanRuntimeMillis == 0.0
                ? 0.0
                : crossDatasetStandardDeviation / averageMeanRuntimeMillis;
        double crossDatasetRuntimeRangeMillis = calculateRange(datasetMeanRuntimeMillis);

        return new AlgorithmTradeoff(
                algorithmName,
                averageMeanRuntimeMillis,
                averageRunCoefficientOfVariation,
                crossDatasetCoefficientOfVariation,
                crossDatasetRuntimeRangeMillis,
                timeComplexity,
                extraSpaceComplexity,
                decisionNote);
    }

    private double calculateStandardDeviation(List<Double> values, double mean) {
        if (values.isEmpty()) {
            return 0.0;
        }

        double squaredDifferenceTotal = 0.0;
        for (double value : values) {
            double difference = value - mean;
            squaredDifferenceTotal += difference * difference;
        }
        return Math.sqrt(squaredDifferenceTotal / values.size());
    }

    private double calculateRange(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }

        double minimum = values.get(0);
        double maximum = values.get(0);
        for (double value : values) {
            if (value < minimum) {
                minimum = value;
            }
            if (value > maximum) {
                maximum = value;
            }
        }
        return maximum - minimum;
    }

    private SortingResult findResultByAlgorithm(List<SortingResult> results, String algorithmName) {
        for (SortingResult result : results) {
            if (result.getAlgorithmName().equals(algorithmName)) {
                return result;
            }
        }
        return null;
    }
}
