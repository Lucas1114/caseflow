package com.caseflow.casework;

import com.caseflow.user.User;
import com.caseflow.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;
import static org.mockito.MockMakers.SUBCLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CaseAssigneeControllerTest {
    private CaseRepository cases;
    private UserRepository users;
    private MockMvc mvc;
    private Case caseItem;
    private User originalUser;
    private User nextUser;

    @BeforeEach
    void setUp() {
        cases = mock(CaseRepository.class, withSettings().mockMaker(SUBCLASS));
        users = mock(UserRepository.class, withSettings().mockMaker(SUBCLASS));
        originalUser = user(1L, "Maya Chen", "maya.chen@example.com");
        nextUser = user(2L, "Noah Williams", "noah.williams@example.com");
        caseItem = new Case();
        ReflectionTestUtils.setField(caseItem, "id", 1L);
        ReflectionTestUtils.setField(caseItem, "title", "Review supplier contract renewal");
        caseItem.updateStatus(CaseStatus.OPEN);
        caseItem.updateAssignee(originalUser);
        when(cases.findByIdWithAssignedUser(1L)).thenReturn(Optional.of(caseItem));
        when(users.findById(2L)).thenReturn(Optional.of(nextUser));
        mvc = MockMvcBuilders.standaloneSetup(new CaseController(new CaseService(cases, users))).build();
    }

    @Test
    void reassignsExistingCaseAndPreservesOtherFields() throws Exception {
        mvc.perform(patch("/api/cases/1/assignee").contentType("application/json")
                        .content("{\"assignedUserId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Review supplier contract renewal"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedUser.id").value(2))
                .andExpect(jsonPath("$.assignedUser.name").value("Noah Williams"))
                .andExpect(jsonPath("$.assignedUser.email").value("noah.williams@example.com"));
        assertSame(nextUser, caseItem.getAssignedUser());
    }

    @Test
    void rejectsMissingCaseBeforeLookingUpUser() throws Exception {
        mvc.perform(patch("/api/cases/999/assignee").contentType("application/json")
                        .content("{\"assignedUserId\":2}"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(users);
    }

    @Test
    void rejectsMissingUserWithoutChangingAssignment() throws Exception {
        mvc.perform(patch("/api/cases/1/assignee").contentType("application/json")
                        .content("{\"assignedUserId\":999}"))
                .andExpect(status().isNotFound());
        assertSame(originalUser, caseItem.getAssignedUser());
    }

    @Test
    void acceptsSavingTheCurrentAssignee() throws Exception {
        when(users.findById(1L)).thenReturn(Optional.of(originalUser));
        mvc.perform(patch("/api/cases/1/assignee").contentType("application/json")
                        .content("{\"assignedUserId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedUser.id").value(1));
    }

    @Test
    void rejectsInvalidIdsBeforeServiceAccess() throws Exception {
        for (String body : new String[]{"{}", "{\"assignedUserId\":null}",
                "{\"assignedUserId\":0}", "{\"assignedUserId\":-1}", "{\"assignedUserId\":\"bad\"}"}) {
            mvc.perform(patch("/api/cases/1/assignee").contentType("application/json").content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(cases, users);
    }

    private User user(Long id, String name, String email) {
        User user = org.springframework.beans.BeanUtils.instantiateClass(User.class);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "name", name);
        ReflectionTestUtils.setField(user, "email", email);
        return user;
    }
}
