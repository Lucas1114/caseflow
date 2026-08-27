package com.caseflow.casework;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCaseActivityRequest(
        @NotBlank @Size(max = 1000) String note
) {
}
