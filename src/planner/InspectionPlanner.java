package planner;

import graph.DijkstraSolver;
import graph.PathQuerySolver;
import graph.WeightedGraph;
import io.CandidateCsvReader;
import io.PathCsvReader;
import model.AlgorithmTradeoff;
import model.DatasetProfile;
import model.GraphQueryCase;
import model.LocationCandidate;
import model.PathMetrics;
import model.ShortestPathResult;
import model.SortingResult;
import model.ValidationCheck;
import sorting.BubbleSort;
import sorting.MergeSort;
import sorting.QuickSort;
import sorting.SortTimer;
import sorting.SortingAlgorithm;
import sorting.TopKSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InspectionPlanner {
    private static final int TOP_TARGET_COUNT = 10;
    private static final int EXPECTED_CANDIDATE_COUNT = 1000;
    private static final int EXPECTED_GRAPH_NODE_COUNT = 1000;
    private static final int EXPECTED_GRAPH_EDGE_COUNT = 2600;
    private static final String DATASET_A = "Dataset A";
    private static final String DATASET_B = "Dataset B";
    private static final String DATASET_C = "Dataset C";
    private static final String CASE_ONE_NAME = "Case 1: A1 to A1";

    private final String dataDirectory;
    private final int measurementRounds;
    private final int warmupRounds;
    private final CandidateCsvReader candidateCsvReader;
    private final PathCsvReader pathCsvReader;
    private final SortTimer sortTimer;
    private final PathQuerySolver pathQuerySolver;
    private final OutputFormatter outputFormatter;
    private final ValidationService validationService;
    private final DatasetProfiler datasetProfiler;
    private final AlgorithmDecisionAnalyzer algorithmDecisionAnalyzer;
    private final PathMetricsCalculator pathMetricsCalculator;
    private final List<SortingAlgorithm> sortingAlgorithms;

    public InspectionPlanner(String dataDirectory, int measurementRounds, int warmupRounds) {
        this.dataDirectory = dataDirectory;
        this.measurementRounds = measurementRounds;
        this.warmupRounds = warmupRounds;
        this.candidateCsvReader = new CandidateCsvReader();
        this.pathCsvReader = new PathCsvReader();
        this.sortTimer = new SortTimer(new TopKSelector());
        this.pathQuerySolver = new PathQuerySolver(new DijkstraSolver());
        this.outputFormatter = new OutputFormatter();
        this.validationService = new ValidationService();
        this.datasetProfiler = new DatasetProfiler();
        this.algorithmDecisionAnalyzer = new AlgorithmDecisionAnalyzer();
        this.pathMetricsCalculator = new PathMetricsCalculator();
        this.sortingAlgorithms = Arrays.<SortingAlgorithm>asList(
                new BubbleSort(),
                new QuickSort(),
                new MergeSort());
    }

    public void run() throws IOException {
        outputFormatter.printHeader(dataDirectory, measurementRounds, warmupRounds);

        Map<String, List<LocationCandidate>> datasets = readCandidateDatasets();
        List<DatasetProfile> datasetProfiles = datasetProfiler.profile(datasets);
        Map<String, List<SortingResult>> sortingResultsByDataset = new LinkedHashMap<String, List<SortingResult>>();
        Map<String, Boolean> sameOrderByDataset = new LinkedHashMap<String, Boolean>();
        Map<String, List<LocationCandidate>> selectedTargetsByDataset = new LinkedHashMap<String, List<LocationCandidate>>();

        for (Map.Entry<String, List<LocationCandidate>> datasetEntry : datasets.entrySet()) {
            List<SortingResult> sortingResults = runSortingAlgorithms(
                    datasetEntry.getKey(),
                    datasetEntry.getValue());
            boolean sameOrder = sortTimer.haveSameOrder(sortingResults);

            sortingResultsByDataset.put(datasetEntry.getKey(), sortingResults);
            sameOrderByDataset.put(datasetEntry.getKey(), sameOrder);

            if (!sameOrder) {
                throw new IllegalStateException(
                        "Sorting algorithms produced different orders for " + datasetEntry.getKey());
            }

            selectedTargetsByDataset.put(datasetEntry.getKey(), findResultByAlgorithm(sortingResults, "Merge Sort").getTopCandidates());
        }

        List<AlgorithmTradeoff> algorithmTradeoffs = algorithmDecisionAnalyzer.analyze(sortingResultsByDataset);
        WeightedGraph graph = pathCsvReader.readGraph(resolveDataFile("paths.csv"));
        List<GraphQueryCase> queryCases = buildRequiredQueryCases(selectedTargetsByDataset);
        Map<String, ShortestPathResult> shortestPathResults = solveQueryCases(graph, queryCases);
        List<PathMetrics> pathMetrics = pathMetricsCalculator.calculate(queryCases, shortestPathResults);
        List<ValidationCheck> validationChecks = validationService.validate(
                datasets,
                sortingResultsByDataset,
                sameOrderByDataset,
                selectedTargetsByDataset,
                graph,
                queryCases,
                shortestPathResults,
                EXPECTED_CANDIDATE_COUNT,
                EXPECTED_GRAPH_NODE_COUNT,
                EXPECTED_GRAPH_EDGE_COUNT);

        outputFormatter.printDatasetSummary(datasets, graph);
        outputFormatter.printDatasetProfiles(datasetProfiles);
        outputFormatter.printSortingRuntimeSummary(sortingResultsByDataset, sameOrderByDataset);
        outputFormatter.printAlgorithmDecisionSupport(algorithmTradeoffs);
        outputFormatter.printSelectedTargets(selectedTargetsByDataset);
        outputFormatter.printGraphWeightSummary(graph);
        outputFormatter.printGraphResults(queryCases, shortestPathResults);
        outputFormatter.printPathMetrics(pathMetrics);
        outputFormatter.printValidationSummary(validationChecks);
    }

    private Map<String, List<LocationCandidate>> readCandidateDatasets() throws IOException {
        Map<String, List<LocationCandidate>> datasets = new LinkedHashMap<String, List<LocationCandidate>>();
        datasets.put(DATASET_A, candidateCsvReader.read(resolveDataFile("candidates_A.csv")));
        datasets.put(DATASET_B, candidateCsvReader.read(resolveDataFile("candidates_B.csv")));
        datasets.put(DATASET_C, candidateCsvReader.read(resolveDataFile("candidates_C.csv")));
        return datasets;
    }

    private List<SortingResult> runSortingAlgorithms(
            String datasetName,
            List<LocationCandidate> originalCandidates) {
        List<SortingResult> results = new ArrayList<SortingResult>();
        for (SortingAlgorithm algorithm : sortingAlgorithms) {
            results.add(sortTimer.measure(
                    datasetName,
                    originalCandidates,
                    algorithm,
                    measurementRounds,
                    warmupRounds,
                    TOP_TARGET_COUNT));
        }
        return results;
    }

    private List<GraphQueryCase> buildRequiredQueryCases(
            Map<String, List<LocationCandidate>> selectedTargetsByDataset) {
        List<LocationCandidate> datasetATargets = selectedTargetsByDataset.get(DATASET_A);
        List<LocationCandidate> datasetBTargets = selectedTargetsByDataset.get(DATASET_B);
        List<LocationCandidate> datasetCTargets = selectedTargetsByDataset.get(DATASET_C);

        String a1 = getLocationId(datasetATargets, 0);
        String a10 = getLocationId(datasetATargets, 9);
        String b1 = getLocationId(datasetBTargets, 0);
        String b5 = getLocationId(datasetBTargets, 4);
        String c1 = getLocationId(datasetCTargets, 0);
        String c5 = getLocationId(datasetCTargets, 4);

        List<GraphQueryCase> cases = new ArrayList<GraphQueryCase>();
        cases.add(new GraphQueryCase(CASE_ONE_NAME, a1, a1, new ArrayList<String>()));
        cases.add(new GraphQueryCase("Case 2: A1 to A10", a1, a10, new ArrayList<String>()));
        cases.add(new GraphQueryCase("Case 3: A1 to B1 via B5", a1, b1, Arrays.asList(b5)));
        cases.add(new GraphQueryCase("Case 4: A1 to C1 via B5 and C5", a1, c1, Arrays.asList(b5, c5)));
        return cases;
    }

    private Map<String, ShortestPathResult> solveQueryCases(
            WeightedGraph graph,
            List<GraphQueryCase> queryCases) {
        Map<String, ShortestPathResult> results = new LinkedHashMap<String, ShortestPathResult>();
        for (GraphQueryCase queryCase : queryCases) {
            results.put(queryCase.getCaseName(), pathQuerySolver.solve(graph, queryCase));
        }
        return results;
    }

    private SortingResult findResultByAlgorithm(List<SortingResult> results, String algorithmName) {
        for (SortingResult result : results) {
            if (result.getAlgorithmName().equals(algorithmName)) {
                return result;
            }
        }
        throw new IllegalArgumentException("Missing sorting result for algorithm: " + algorithmName);
    }

    private String getLocationId(List<LocationCandidate> candidates, int index) {
        if (candidates.size() <= index) {
            throw new IllegalArgumentException("Not enough selected targets to access rank " + (index + 1));
        }
        return candidates.get(index).getLocationId();
    }

    private String resolveDataFile(String fileName) {
        return new File(dataDirectory, fileName).getPath();
    }
}
