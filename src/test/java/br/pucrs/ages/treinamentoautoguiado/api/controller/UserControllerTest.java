package br.pucrs.ages.treinamentoautoguiado.api.controller;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.responses.UserResponse;
import br.pucrs.ages.treinamentoautoguiado.api.service.UserService;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiExceptionHandler;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void testAuthenticatedUser_ShouldReturnUserResponse() throws Exception {
        User user = new User("test@email.com", "password", "5551981070960", "test");
        UserResponse userResponse = new UserResponse(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUsers_WhenUserIsAdmin() throws Exception {
        List<UserResponse> users = List.of(new UserResponse(new User("admin@email.com", "password", "5551981070960", "test")));
        when(userService.fetchAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()));
    }

    @Test
    void testDeleteUser_ShouldReturnNoContent_WhenUserIsDeleted() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        Long userId = 99L;

        doThrow(new ApiRequestException("Usuário não encontrado"))
                .when(userService).deleteUserById(userId);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testUpdateUser_ShouldReturnUserResponse_WhenUserIsUpdated() throws Exception {
        User originalUser = new User("test@email.com", "password", "5551981070960", "test");
        originalUser.setId(1L);

        when(userService.updateUser(any(), any(), any())).thenReturn(originalUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getPrincipal()).thenReturn(originalUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(originalUser.getEmail()))
                .andExpect(jsonPath("$.is_first_access").value(originalUser.getIsFirstAccess()));
    }
}
