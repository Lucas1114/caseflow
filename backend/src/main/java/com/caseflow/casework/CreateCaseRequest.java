package com.caseflow.casework;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCaseRequest(
        @NotBlank @Size(max = 200) String title,
        @NotNull @Positive Long assignedUserId
) {
}
