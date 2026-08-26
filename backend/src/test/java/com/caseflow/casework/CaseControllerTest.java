package com.caseflow.casework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CaseControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CaseController(new StubCaseService())).build();
    }

    @Test
    void returnsCaseDetails() throws Exception {
        CaseResponse response = new CaseResponse(
                1L,
                "Review supplier contract renewal",
                CaseStatus.OPEN,
                new AssignedUserResponse(1L, "Maya Chen", "maya.chen@example.com")
        );

        StubCaseService.caseResponse = response;

        mockMvc.perform(get("/api/cases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Review supplier contract renewal"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedUser.name").value("Maya Chen"))
                .andExpect(jsonPath("$.assignedUser.email").value("maya.chen@example.com"));
    }

    @Test
    void returnsNotFoundWhenCaseDoesNotExist() throws Exception {
        StubCaseService.caseResponse = null;

        mockMvc.perform(get("/api/cases/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatesCaseStatus() throws Exception {
        StubCaseService.caseResponse = new CaseResponse(
                1L,
                "Review supplier contract renewal",
                CaseStatus.IN_PROGRESS,
                new AssignedUserResponse(1L, "Maya Chen", "maya.chen@example.com")
        );

        mockMvc.perform(patch("/api/cases/1/status")
                        .contentType("application/json")
                        .content("""
                                {"status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingCase() throws Exception {
        StubCaseService.caseResponse = null;

        mockMvc.perform(patch("/api/cases/999/status")
                        .contentType("application/json")
                        .content("""
                                {"status":"CLOSED"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidStatus() throws Exception {
        mockMvc.perform(patch("/api/cases/1/status")
                        .contentType("application/json")
                        .content("""
                                {"status":"WAITING"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingStatus() throws Exception {
        mockMvc.perform(patch("/api/cases/1/status")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private static class StubCaseService extends CaseService {

        private static CaseResponse caseResponse;

        StubCaseService() {
            super(null);
        }

        @Override
        public CaseResponse findById(Long id) {
            if (caseResponse == null) {
                throw new ResponseStatusException(NOT_FOUND, "Case not found");
            }

            return caseResponse;
        }

        @Override
        public CaseResponse updateStatus(Long id, CaseStatus status) {
            if (caseResponse == null) {
                throw new ResponseStatusException(NOT_FOUND, "Case not found");
            }

            return caseResponse;
        }
    }
}
