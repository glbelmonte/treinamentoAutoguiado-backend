package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.dto.UpdateUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.model.Role;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.UserResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("test1@example.com", "123456","5551981070960","test");
        user2 = new User("test2@example.com", "123456","5551981070960", "test");
    }

    @Test
    void testFetchAllUsers_shouldReturnUserResponses_whenUsersExist() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserResponse> userResponses = userService.fetchAllUsers();

        assertThat(userResponses).isNotEmpty().hasSize(2);
        assertThat(userResponses.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(userResponses.get(1).getEmail()).isEqualTo("test2@example.com");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFetchAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> userResponses = userService.fetchAllUsers();

        assertThat(userResponses).isEmpty();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUserById_shouldMarkUserAsDeleted_whenExists() {
        user1.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.deleteUserById(1L);

        assertThat(user1.getIsDeleted()).isTrue();

        verify(userRepository, times(1)).save(user1);

        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUserById_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateUser_shouldThrowException_whenUserHasNoPermission() {
        Long userId = 1L;
        User currentUser = new User("admin@example.com", "123456", "5551981070960", "admin");
        currentUser.setRole(Role.USER);

        UpdateUserDTO updateUserDto = new UpdateUserDTO(true);

        assertThatThrownBy(() -> userService.updateUser(userId, currentUser, updateUserDto))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Sem permissão para acessar esse recurso");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserIsAuthorized() {
        Long userId = 1L;
        User currentUser = new User("admin@example.com", "123456", "5551981070960", "admin");
        currentUser.setRole(Role.ADMIN);

        User userToUpdate = new User("test@example.com", "123456", "5551981070960", "test");
        userToUpdate.setId(userId);

        UpdateUserDTO updateUserDto = new UpdateUserDTO(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));

        User updatedUser = userService.updateUser(userId, currentUser, updateUserDto);

        assertThat(updatedUser.getIsFirstAccess()).isTrue();
        verify(userRepository, times(1)).save(userToUpdate);
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        Long userId = 99L;
        User currentUser = new User("admin@example.com", "123456", "5551981070960", "admin");
        currentUser.setId(1L);
        currentUser.setRole(Role.ADMIN);

        UpdateUserDTO updateUserDto = new UpdateUserDTO(true);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, currentUser, updateUserDto))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldUpdateFirstAccess_whenProvided() {
        Long userId = 1L;
        User currentUser = new User("user@example.com", "123456", "5551981070960", "test");
        currentUser.setId(userId);

        User userToUpdate = new User("test@example.com", "123456", "5551981070960", "test");
        userToUpdate.setId(userId);

        UpdateUserDTO updateUserDto = new UpdateUserDTO(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));

        User updatedUser = userService.updateUser(userId, currentUser, updateUserDto);

        assertThat(updatedUser.getIsFirstAccess()).isFalse();
        verify(userRepository, times(1)).save(userToUpdate);
    }

    @Test
    void updateUser_shouldNotUpdate_whenNoValueProvided() {
        Long userId = 1L;
        User currentUser = new User("user@example.com", "123456", "5551981070960", "test");
        currentUser.setId(userId);

        User userToUpdate = new User("test@example.com", "123456", "5551981070960", "test");
        userToUpdate.setId(userId);

        UpdateUserDTO updateUserDto = new UpdateUserDTO(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));

        userService.updateUser(userId, currentUser, updateUserDto);

        verify(userRepository, times(1)).save(userToUpdate);
    }
}
