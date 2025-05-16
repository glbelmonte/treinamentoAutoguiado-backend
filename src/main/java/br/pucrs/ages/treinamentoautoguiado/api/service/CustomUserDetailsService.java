package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws ApiRequestException {
        return userRepository.findByEmailAndIsDeletedFalse(username).orElseThrow(() -> new ApiRequestException("Nenhum usuário encontrado com o e-mail: " + username));
    }
}
