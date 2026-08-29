package com.caseflow.casework;

public record CaseSummaryResponse(
        long total,
        long open,
        long inProgress,
        long resolved,
        long closed
) {
}
