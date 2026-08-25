package com.caseflow.casework;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{id}")
    public CaseResponse findById(@PathVariable Long id) {
        return caseService.findById(id);
    }
}
