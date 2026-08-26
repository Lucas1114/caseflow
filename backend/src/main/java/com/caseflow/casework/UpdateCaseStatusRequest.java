package com.caseflow.casework;

import jakarta.validation.constraints.NotNull;

public record UpdateCaseStatusRequest(
        @NotNull CaseStatus status
) {
}
