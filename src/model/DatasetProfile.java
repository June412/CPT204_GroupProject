package model;

public class DatasetProfile {
    private final String datasetName;
    private final int rowCount;
    private final int uniquePriorityScoreCount;
    private final int duplicateScoreRowCount;
    private final int adjacentOutOfOrderPairCount;
    private final double adjacentCorrectOrderPercentage;
    private final double adjacentOutOfOrderPercentage;
    private final boolean alreadySorted;
    private final String inputOrderDescription;

    public DatasetProfile(
            String datasetName,
            int rowCount,
            int uniquePriorityScoreCount,
            int duplicateScoreRowCount,
            int adjacentOutOfOrderPairCount,
            double adjacentCorrectOrderPercentage,
            double adjacentOutOfOrderPercentage,
            boolean alreadySorted,
            String inputOrderDescription) {
        this.datasetName = datasetName;
        this.rowCount = rowCount;
        this.uniquePriorityScoreCount = uniquePriorityScoreCount;
        this.duplicateScoreRowCount = duplicateScoreRowCount;
        this.adjacentOutOfOrderPairCount = adjacentOutOfOrderPairCount;
        this.adjacentCorrectOrderPercentage = adjacentCorrectOrderPercentage;
        this.adjacentOutOfOrderPercentage = adjacentOutOfOrderPercentage;
        this.alreadySorted = alreadySorted;
        this.inputOrderDescription = inputOrderDescription;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getUniquePriorityScoreCount() {
        return uniquePriorityScoreCount;
    }

    public int getDuplicateScoreRowCount() {
        return duplicateScoreRowCount;
    }

    public int getAdjacentOutOfOrderPairCount() {
        return adjacentOutOfOrderPairCount;
    }

    public double getAdjacentCorrectOrderPercentage() {
        return adjacentCorrectOrderPercentage;
    }

    public double getAdjacentOutOfOrderPercentage() {
        return adjacentOutOfOrderPercentage;
    }

    public boolean isAlreadySorted() {
        return alreadySorted;
    }

    public String getInputOrderDescription() {
        return inputOrderDescription;
    }
}
