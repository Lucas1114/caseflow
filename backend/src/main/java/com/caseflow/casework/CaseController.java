package com.caseflow.casework;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public List<CaseResponse> findAll() {
        return caseService.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CaseResponse create(@Valid @RequestBody CreateCaseRequest request) {
        return caseService.create(request.title(), request.assignedUserId());
    }

    @GetMapping("/summary")
    public CaseSummaryResponse getSummary() {
        return caseService.getSummary();
    }

    @GetMapping("/{id}")
    public CaseResponse findById(@PathVariable Long id) {
        return caseService.findById(id);
    }

    @PatchMapping("/{id}/status")
    public CaseResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseStatusRequest request
    ) {
        return caseService.updateStatus(id, request.status());
    }

    @PatchMapping("/{id}/assignee")
    public CaseResponse updateAssignee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseAssigneeRequest request
    ) {
        return caseService.updateAssignee(id, request.assignedUserId());
    }
}
