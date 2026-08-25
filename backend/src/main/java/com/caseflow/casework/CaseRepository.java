package com.caseflow.casework;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends JpaRepository<Case, Long> {

    @Override
    @EntityGraph(attributePaths = "assignedUser")
    List<Case> findAll();

    @EntityGraph(attributePaths = "assignedUser")
    @Query("select caseItem from Case caseItem where caseItem.id = :id")
    Optional<Case> findByIdWithAssignedUser(@Param("id") Long id);
}
