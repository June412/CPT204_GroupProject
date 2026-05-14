package planner;

import model.DatasetProfile;
import model.LocationCandidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetProfiler {
    public List<DatasetProfile> profile(Map<String, List<LocationCandidate>> datasets) {
        List<DatasetProfile> profiles = new ArrayList<DatasetProfile>();
        for (Map.Entry<String, List<LocationCandidate>> entry : datasets.entrySet()) {
            profiles.add(profileDataset(entry.getKey(), entry.getValue()));
        }
        return profiles;
    }

    private DatasetProfile profileDataset(String datasetName, List<LocationCandidate> candidates) {
        Map<Integer, Integer> scoreCounts = countPriorityScores(candidates);
        int duplicateScoreRows = countDuplicateScoreRows(scoreCounts);
        int correctAdjacentPairs = countCorrectAdjacentPairs(candidates);
        int adjacentPairCount = Math.max(0, candidates.size() - 1);
        int outOfOrderAdjacentPairs = adjacentPairCount - correctAdjacentPairs;
        double adjacentCorrectPercentage = adjacentPairCount == 0
                ? 100.0
                : correctAdjacentPairs * 100.0 / adjacentPairCount;
        double adjacentOutOfOrderPercentage = adjacentPairCount == 0
                ? 0.0
                : outOfOrderAdjacentPairs * 100.0 / adjacentPairCount;
        boolean alreadySorted = correctAdjacentPairs == adjacentPairCount;

        return new DatasetProfile(
                datasetName,
                candidates.size(),
                scoreCounts.size(),
                duplicateScoreRows,
                outOfOrderAdjacentPairs,
                adjacentCorrectPercentage,
                adjacentOutOfOrderPercentage,
                alreadySorted,
                describeInputOrder(alreadySorted, adjacentCorrectPercentage, duplicateScoreRows));
    }

    private Map<Integer, Integer> countPriorityScores(List<LocationCandidate> candidates) {
        Map<Integer, Integer> scoreCounts = new HashMap<Integer, Integer>();
        for (LocationCandidate candidate : candidates) {
            int score = candidate.getPriorityScore();
            Integer currentCount = scoreCounts.get(score);
            if (currentCount == null) {
                scoreCounts.put(score, 1);
            } else {
                scoreCounts.put(score, currentCount + 1);
            }
        }
        return scoreCounts;
    }

    private int countDuplicateScoreRows(Map<Integer, Integer> scoreCounts) {
        int duplicateRows = 0;
        for (int count : scoreCounts.values()) {
            if (count > 1) {
                duplicateRows += count;
            }
        }
        return duplicateRows;
    }

    private int countCorrectAdjacentPairs(List<LocationCandidate> candidates) {
        int correctPairs = 0;
        for (int index = 1; index < candidates.size(); index++) {
            if (candidates.get(index - 1).compareTo(candidates.get(index)) <= 0) {
                correctPairs++;
            }
        }
        return correctPairs;
    }

    private String describeInputOrder(
            boolean alreadySorted,
            double adjacentCorrectPercentage,
            int duplicateScoreRows) {
        if (alreadySorted) {
            return "Already sorted";
        }
        if (adjacentCorrectPercentage >= 90.0) {
            return "Nearly sorted";
        }
        if (duplicateScoreRows > 0) {
            return "Mixed order with many ties";
        }
        return "Mixed order";
    }
}
