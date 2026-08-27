package com.caseflow.casework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CaseActivityControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StubCaseActivityService.caseExists = true;
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CaseActivityController(new StubCaseActivityService()))
                .build();
    }

    @Test
    void returnsActivitiesNewestFirst() throws Exception {
        StubCaseActivityService.activities = List.of(
                new CaseActivityResponse(2L, "Newest note", Instant.parse("2026-08-27T02:00:00Z")),
                new CaseActivityResponse(1L, "Older note", Instant.parse("2026-08-26T02:00:00Z"))
        );

        mockMvc.perform(get("/api/cases/1/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].note").value("Newest note"))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    @Test
    void createsActivity() throws Exception {
        StubCaseActivityService.createdActivity = new CaseActivityResponse(
                3L,
                "Customer supplied the missing document.",
                Instant.parse("2026-08-27T03:00:00Z")
        );

        mockMvc.perform(post("/api/cases/1/activities")
                        .contentType("application/json")
                        .content("""
                                {"note":"Customer supplied the missing document."}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.note").value("Customer supplied the missing document."))
                .andExpect(jsonPath("$.createdAt").value("2026-08-27T03:00:00Z"));
    }

    @Test
    void returnsNotFoundWhenCaseDoesNotExist() throws Exception {
        StubCaseActivityService.caseExists = false;

        mockMvc.perform(get("/api/cases/999/activities"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBlankNote() throws Exception {
        mockMvc.perform(post("/api/cases/1/activities")
                        .contentType("application/json")
                        .content("""
                                {"note":"   "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingNote() throws Exception {
        mockMvc.perform(post("/api/cases/1/activities")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsOversizedNote() throws Exception {
        String oversizedNote = "a".repeat(1001);

        mockMvc.perform(post("/api/cases/1/activities")
                        .contentType("application/json")
                        .content("{\"note\":\"" + oversizedNote + "\"}"))
                .andExpect(status().isBadRequest());
    }

    private static class StubCaseActivityService extends CaseActivityService {

        private static boolean caseExists;
        private static List<CaseActivityResponse> activities;
        private static CaseActivityResponse createdActivity;

        StubCaseActivityService() {
            super(null, null);
        }

        @Override
        public List<CaseActivityResponse> findAll(Long caseId) {
            requireExistingCase();
            return activities;
        }

        @Override
        public CaseActivityResponse create(Long caseId, String note) {
            requireExistingCase();
            return createdActivity;
        }

        private void requireExistingCase() {
            if (!caseExists) {
                throw new ResponseStatusException(NOT_FOUND, "Case not found");
            }
        }
    }
}
