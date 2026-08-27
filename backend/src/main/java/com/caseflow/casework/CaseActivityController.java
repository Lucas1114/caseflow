package com.caseflow.casework;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/activities")
public class CaseActivityController {

    private final CaseActivityService activityService;

    public CaseActivityController(CaseActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<CaseActivityResponse> findAll(@PathVariable Long caseId) {
        return activityService.findAll(caseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseActivityResponse create(
            @PathVariable Long caseId,
            @Valid @RequestBody CreateCaseActivityRequest request
    ) {
        return activityService.create(caseId, request.note());
    }
}
