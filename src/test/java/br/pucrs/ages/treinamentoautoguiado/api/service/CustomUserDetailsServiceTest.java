package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "123456","5551981070960","test");
    }

    @Test
    void testLoadUserByUsername_shouldReturnUser_whenUserExists() throws ApiRequestException {
        when(userRepository.findByEmailAndIsDeletedFalse("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());

        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse("test@example.com");
    }

    @Test
    void testLoadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByEmailAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(
                ApiRequestException.class, () -> customUserDetailsService.loadUserByUsername("xxxxx@example.com"));

        assertEquals("Nenhum usuário encontrado com o e-mail: xxxxx@example.com", exception.getMessage());

        verify(userRepository, times(1)).findByEmailAndIsDeletedFalse("xxxxx@example.com");
    }
}
