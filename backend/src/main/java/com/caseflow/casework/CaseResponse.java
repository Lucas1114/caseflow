package com.caseflow.casework;

public record CaseResponse(
        Long id,
        String title,
        CaseStatus status,
        AssignedUserResponse assignedUser
) {
}
