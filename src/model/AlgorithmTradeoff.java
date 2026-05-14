package model;

public class AlgorithmTradeoff {
    private final String algorithmName;
    private final double averageMeanRuntimeMillis;
    private final double averageRunCoefficientOfVariation;
    private final double crossDatasetCoefficientOfVariation;
    private final double crossDatasetRuntimeRangeMillis;
    private final String timeComplexity;
    private final String extraSpaceComplexity;
    private final String decisionNote;

    public AlgorithmTradeoff(
            String algorithmName,
            double averageMeanRuntimeMillis,
            double averageRunCoefficientOfVariation,
            double crossDatasetCoefficientOfVariation,
            double crossDatasetRuntimeRangeMillis,
            String timeComplexity,
            String extraSpaceComplexity,
            String decisionNote) {
        this.algorithmName = algorithmName;
        this.averageMeanRuntimeMillis = averageMeanRuntimeMillis;
        this.averageRunCoefficientOfVariation = averageRunCoefficientOfVariation;
        this.crossDatasetCoefficientOfVariation = crossDatasetCoefficientOfVariation;
        this.crossDatasetRuntimeRangeMillis = crossDatasetRuntimeRangeMillis;
        this.timeComplexity = timeComplexity;
        this.extraSpaceComplexity = extraSpaceComplexity;
        this.decisionNote = decisionNote;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double getAverageMeanRuntimeMillis() {
        return averageMeanRuntimeMillis;
    }

    public double getAverageRunCoefficientOfVariation() {
        return averageRunCoefficientOfVariation;
    }

    public double getCrossDatasetCoefficientOfVariation() {
        return crossDatasetCoefficientOfVariation;
    }

    public double getCrossDatasetRuntimeRangeMillis() {
        return crossDatasetRuntimeRangeMillis;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getExtraSpaceComplexity() {
        return extraSpaceComplexity;
    }

    public String getDecisionNote() {
        return decisionNote;
    }
}
