package com.caseflow.casework;

import com.caseflow.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.MockMakers.SUBCLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CaseSummaryControllerTest {

    private CaseRepository cases;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        cases = mock(CaseRepository.class, withSettings().mockMaker(SUBCLASS));
        UserRepository users = mock(UserRepository.class, withSettings().mockMaker(SUBCLASS));
        mvc = MockMvcBuilders.standaloneSetup(
                new CaseController(new CaseService(cases, users))
        ).build();
    }

    @Test
    void returnsTotalAndCountsForEveryWorkflowStatus() throws Exception {
        when(cases.countByStatus()).thenReturn(List.of(
                new CaseStatusCount(CaseStatus.OPEN, 3),
                new CaseStatusCount(CaseStatus.IN_PROGRESS, 2),
                new CaseStatusCount(CaseStatus.RESOLVED, 1),
                new CaseStatusCount(CaseStatus.CLOSED, 4)
        ));

        mvc.perform(get("/api/cases/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.open").value(3))
                .andExpect(jsonPath("$.inProgress").value(2))
                .andExpect(jsonPath("$.resolved").value(1))
                .andExpect(jsonPath("$.closed").value(4));

        verify(cases).countByStatus();
    }

    @Test
    void returnsZeroForStatusesMissingFromTheGroupedQuery() throws Exception {
        when(cases.countByStatus()).thenReturn(List.of(
                new CaseStatusCount(CaseStatus.OPEN, 2)
        ));

        mvc.perform(get("/api/cases/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.open").value(2))
                .andExpect(jsonPath("$.inProgress").value(0))
                .andExpect(jsonPath("$.resolved").value(0))
                .andExpect(jsonPath("$.closed").value(0));
    }
}
