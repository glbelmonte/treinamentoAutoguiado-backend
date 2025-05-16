package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.dto.UpdateUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.model.Role;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.UserResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();

        for (User user : users) {
            userResponses.add(new UserResponse(user));
        }

        return userResponses;
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Usuário não encontrado"));

        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User updateUser(Long id, User currentUser, UpdateUserDTO updateUserDto) {
        if (!Objects.equals(currentUser.getId(), id) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new ApiRequestException("Sem permissão para acessar esse recurso");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ApiRequestException("Usuário não encontrado"));

        if (updateUserDto.getIsFirstAccess() != null) {
            user.setIsFirstAccess(updateUserDto.getIsFirstAccess());
        }

        userRepository.save(user);
        return user;
    }
}
