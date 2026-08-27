package com.caseflow.casework;

import java.time.Instant;

public record CaseActivityResponse(
        Long id,
        String note,
        Instant createdAt
) {
}
