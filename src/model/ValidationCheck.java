package model;

public class ValidationCheck {
    private final String checkName;
    private final boolean passed;
    private final String details;

    public ValidationCheck(String checkName, boolean passed, String details) {
        if (checkName == null || checkName.trim().isEmpty()) {
            throw new IllegalArgumentException("checkName must not be empty");
        }
        this.checkName = checkName.trim();
        this.passed = passed;
        this.details = details == null ? "" : details.trim();
    }

    public String getCheckName() {
        return checkName;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getDetails() {
        return details;
    }
}
