package com.caseflow.casework;

import com.caseflow.user.User;
import com.caseflow.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;

    public CaseService(CaseRepository caseRepository, UserRepository userRepository) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public CaseResponse updateStatus(Long id, CaseStatus status) {
        Case caseItem = caseRepository.findByIdWithAssignedUser(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Case not found"));

        caseItem.updateStatus(status);
        return toResponse(caseItem);
    }

    @Transactional
    public CaseResponse updateAssignee(Long id, Long assignedUserId) {
        Case caseItem = caseRepository.findByIdWithAssignedUser(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Case not found"));
        User assignedUser = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        caseItem.updateAssignee(assignedUser);
        return toResponse(caseItem);
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
