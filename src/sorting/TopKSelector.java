package sorting;

import java.util.ArrayList;
import java.util.List;

import model.LocationCandidate;

public class TopKSelector {
    public List<LocationCandidate> selectTop(List<LocationCandidate> sortedCandidates, int k) {
        // Avoid asking for more rows than the list actually has
        int limit = Math.min(k, sortedCandidates.size());
        return new ArrayList<LocationCandidate>(sortedCandidates.subList(0, limit));
    }
}
