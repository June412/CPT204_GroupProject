package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingResult {
    private final String datasetName;
    private final String algorithmName;
    private final double meanRuntimeNanos;
    private final double standardDeviationRuntimeNanos;
    private final double medianRuntimeNanos;
    private final long minRuntimeNanos;
    private final long maxRuntimeNanos;
    private final double coefficientOfVariation;
    private final int measurementRounds;
    private final int warmupRounds;
    private final List<LocationCandidate> sortedCandidates;
    private final List<LocationCandidate> topCandidates;

    public SortingResult(
            String datasetName,
            String algorithmName,
            double meanRuntimeNanos,
            double standardDeviationRuntimeNanos,
            double medianRuntimeNanos,
            long minRuntimeNanos,
            long maxRuntimeNanos,
            double coefficientOfVariation,
            int measurementRounds,
            int warmupRounds,
            List<LocationCandidate> sortedCandidates,
            List<LocationCandidate> topCandidates) {
        this.datasetName = datasetName;
        this.algorithmName = algorithmName;
        this.meanRuntimeNanos = meanRuntimeNanos;
        this.standardDeviationRuntimeNanos = standardDeviationRuntimeNanos;
        this.medianRuntimeNanos = medianRuntimeNanos;
        this.minRuntimeNanos = minRuntimeNanos;
        this.maxRuntimeNanos = maxRuntimeNanos;
        this.coefficientOfVariation = coefficientOfVariation;
        this.measurementRounds = measurementRounds;
        this.warmupRounds = warmupRounds;
        this.sortedCandidates = Collections.unmodifiableList(new ArrayList<LocationCandidate>(sortedCandidates));
        this.topCandidates = Collections.unmodifiableList(new ArrayList<LocationCandidate>(topCandidates));
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double getMeanRuntimeNanos() {
        return meanRuntimeNanos;
    }

    public double getStandardDeviationRuntimeNanos() {
        return standardDeviationRuntimeNanos;
    }

    public double getMedianRuntimeNanos() {
        return medianRuntimeNanos;
    }

    public long getMinRuntimeNanos() {
        return minRuntimeNanos;
    }

    public long getMaxRuntimeNanos() {
        return maxRuntimeNanos;
    }

    public double getCoefficientOfVariation() {
        return coefficientOfVariation;
    }

    public int getMeasurementRounds() {
        return measurementRounds;
    }

    public int getWarmupRounds() {
        return warmupRounds;
    }

    public List<LocationCandidate> getSortedCandidates() {
        return sortedCandidates;
    }

    public List<LocationCandidate> getTopCandidates() {
        return topCandidates;
    }
}
