package com.caseflow.casework;

import com.caseflow.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> findAll() {
        return caseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseResponse findById(Long id) {
        return caseRepository.findByIdWithAssignedUser(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Case not found"));
    }

    private CaseResponse toResponse(Case caseItem) {
        User assignedUser = caseItem.getAssignedUser();

        return new CaseResponse(
                caseItem.getId(),
                caseItem.getTitle(),
                caseItem.getStatus(),
                new AssignedUserResponse(
                        assignedUser.getId(),
                        assignedUser.getName(),
                        assignedUser.getEmail()
                )
        );
    }
}
