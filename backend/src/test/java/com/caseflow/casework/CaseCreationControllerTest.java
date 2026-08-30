package com.caseflow.casework;

import com.caseflow.user.User;
import com.caseflow.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockMakers.SUBCLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CaseCreationControllerTest {

    private CaseRepository cases;
    private UserRepository users;
    private MockMvc mvc;
    private User assignedUser;

    @BeforeEach
    void setUp() {
        cases = mock(CaseRepository.class, org.mockito.Mockito.withSettings().mockMaker(SUBCLASS));
        users = mock(UserRepository.class, org.mockito.Mockito.withSettings().mockMaker(SUBCLASS));
        assignedUser = org.springframework.beans.BeanUtils.instantiateClass(User.class);
        ReflectionTestUtils.setField(assignedUser, "id", 1L);
        ReflectionTestUtils.setField(assignedUser, "name", "Maya Chen");
        ReflectionTestUtils.setField(assignedUser, "email", "maya.chen@example.com");
        when(users.findById(1L)).thenReturn(Optional.of(assignedUser));
        when(cases.save(any(Case.class))).thenAnswer(invocation -> {
            Case saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 4L);
            return saved;
        });
        mvc = MockMvcBuilders.standaloneSetup(new CaseController(new CaseService(cases, users))).build();
    }

    @Test
    void createsOpenCaseWithTrimmedTitleAndExistingAssignee() throws Exception {
        mvc.perform(post("/api/cases").contentType("application/json")
                        .content("""
                                {"title":"  Investigate delayed customer refund  ","assignedUserId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Investigate delayed customer refund"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedUser.id").value(1))
                .andExpect(jsonPath("$.assignedUser.name").value("Maya Chen"));

        var captor = org.mockito.ArgumentCaptor.forClass(Case.class);
        verify(cases).save(captor.capture());
        assertEquals("Investigate delayed customer refund", captor.getValue().getTitle());
        assertEquals(CaseStatus.OPEN, captor.getValue().getStatus());
        assertSame(assignedUser, captor.getValue().getAssignedUser());
    }

    @Test
    void rejectsMissingUserWithoutSaving() throws Exception {
        mvc.perform(post("/api/cases").contentType("application/json")
                        .content("{\"title\":\"Investigate refund\",\"assignedUserId\":999}"))
                .andExpect(status().isNotFound());

        verify(cases, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void rejectsInvalidTitlesBeforeServiceAccess() throws Exception {
        String oversizedTitle = "a".repeat(201);
        for (String body : new String[]{
                "{\"assignedUserId\":1}",
                "{\"title\":null,\"assignedUserId\":1}",
                "{\"title\":\"\",\"assignedUserId\":1}",
                "{\"title\":\"   \",\"assignedUserId\":1}",
                "{\"title\":\"" + oversizedTitle + "\",\"assignedUserId\":1}"
        }) {
            mvc.perform(post("/api/cases").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(users);
        verifyNoInteractions(cases);
    }

    @Test
    void rejectsInvalidAssigneeIdsBeforeServiceAccess() throws Exception {
        for (String body : new String[]{
                "{\"title\":\"Investigate refund\"}",
                "{\"title\":\"Investigate refund\",\"assignedUserId\":null}",
                "{\"title\":\"Investigate refund\",\"assignedUserId\":0}",
                "{\"title\":\"Investigate refund\",\"assignedUserId\":-1}",
                "{\"title\":\"Investigate refund\",\"assignedUserId\":\"bad\"}"
        }) {
            mvc.perform(post("/api/cases").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(users);
        verifyNoInteractions(cases);
    }
}
