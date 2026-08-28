package com.caseflow.casework;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCaseAssigneeRequest(@NotNull @Positive Long assignedUserId) {
}
