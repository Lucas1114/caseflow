package com.caseflow.casework;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    @Override
    @EntityGraph(attributePaths = "assignedUser")
    List<Case> findAll();
}
