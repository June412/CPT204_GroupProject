package app;

import planner.InspectionPlanner;

public class Main {
    private static final String DEFAULT_DATA_DIRECTORY = "Data";
    private static final int DEFAULT_MEASUREMENT_ROUNDS = 30;
    private static final int DEFAULT_WARMUP_ROUNDS = 5;

    public static void main(String[] args) {
        String dataDirectory = args.length >= 1 ? args[0] : DEFAULT_DATA_DIRECTORY;
        int measurementRounds = args.length >= 2
                ? parsePositiveInteger(args[1], "measurement rounds", DEFAULT_MEASUREMENT_ROUNDS)
                : DEFAULT_MEASUREMENT_ROUNDS;
        int warmupRounds = args.length >= 3
                ? parseNonNegativeInteger(args[2], "warm-up rounds", DEFAULT_WARMUP_ROUNDS)
                : DEFAULT_WARMUP_ROUNDS;

        try {
            InspectionPlanner planner = new InspectionPlanner(dataDirectory, measurementRounds, warmupRounds);
            planner.run();
        } catch (Exception exception) {
            System.err.println("Application failed: " + exception.getMessage());
            exception.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static int parsePositiveInteger(String value, String label, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                System.out.println(label + " must be positive. Using default: " + defaultValue);
                return defaultValue;
            }
            return parsed;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid " + label + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    private static int parseNonNegativeInteger(String value, String label, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 0) {
                System.out.println(label + " must not be negative. Using default: " + defaultValue);
                return defaultValue;
            }
            return parsed;
        } catch (NumberFormatException exception) {
            System.out.println("Invalid " + label + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }
}
