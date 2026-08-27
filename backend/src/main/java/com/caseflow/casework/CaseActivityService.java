package com.caseflow.casework;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CaseActivityService {

    private final CaseRepository caseRepository;
    private final CaseActivityRepository activityRepository;

    public CaseActivityService(
            CaseRepository caseRepository,
            CaseActivityRepository activityRepository
    ) {
        this.caseRepository = caseRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<CaseActivityResponse> findAll(Long caseId) {
        requireCase(caseId);

        return activityRepository.findByCaseItemIdOrderByCreatedAtDescIdDesc(caseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CaseActivityResponse create(Long caseId, String note) {
        Case caseItem = requireCase(caseId);
        CaseActivity activity = new CaseActivity(caseItem, note.trim(), Instant.now());

        return toResponse(activityRepository.save(activity));
    }

    private Case requireCase(Long caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Case not found"));
    }

    private CaseActivityResponse toResponse(CaseActivity activity) {
        return new CaseActivityResponse(
                activity.getId(),
                activity.getNote(),
                activity.getCreatedAt()
        );
    }
}
