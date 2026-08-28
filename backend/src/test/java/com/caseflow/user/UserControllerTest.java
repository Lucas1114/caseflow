package com.caseflow.user;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockMakers.SUBCLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {
    @Test
    void listsExistingUsers() throws Exception {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "name", "Maya Chen");
        ReflectionTestUtils.setField(user, "email", "maya.chen@example.com");
        UserRepository repository = mock(UserRepository.class, withSettings().mockMaker(SUBCLASS));
        when(repository.findAll()).thenReturn(List.of(user));

        MockMvcBuilders.standaloneSetup(new UserController(new UserService(repository))).build()
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maya Chen"))
                .andExpect(jsonPath("$[0].email").value("maya.chen@example.com"));
    }

    @Test
    void returnsEmptyListWhenNoUsersExist() throws Exception {
        UserRepository repository = mock(UserRepository.class, withSettings().mockMaker(SUBCLASS));
        when(repository.findAll()).thenReturn(List.of());
        MockMvcBuilders.standaloneSetup(new UserController(new UserService(repository))).build()
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
