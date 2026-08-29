package com.caseflow.casework;

public record CaseStatusCount(
        CaseStatus status,
        long count
) {
}
