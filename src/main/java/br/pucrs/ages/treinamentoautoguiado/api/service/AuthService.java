package br.pucrs.ages.treinamentoautoguiado.api.service;

import br.pucrs.ages.treinamentoautoguiado.api.dto.LoginUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.dto.RegisterUserDTO;
import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import br.pucrs.ages.treinamentoautoguiado.api.repository.UserRepository;
import br.pucrs.ages.treinamentoautoguiado.api.responses.AuthResponse;
import br.pucrs.ages.treinamentoautoguiado.api.util.ApiRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signup(RegisterUserDTO input) {
        Optional<User> user = userRepository.findByEmailAndIsDeletedFalse(input.getEmail());

        if (user.isPresent()) {
            throw new ApiRequestException("Já existe um usuário cadastrado com este e-mail");
        }

        User newUser = new User(input.getEmail(), passwordEncoder.encode(input.getPassword()),input.getCpf(),input.getNome());
        newUser = userRepository.save(newUser);

        String accessToken = jwtService.generateToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(newUser);
        Date expiresIn = jwtService.extractExpiration(accessToken);

        return new AuthResponse("success", "Usuário cadastrado com sucesso", newUser, accessToken, refreshToken, expiresIn);
    }

    public AuthResponse signin(LoginUserDTO loginUserDTO) {
        User user = userRepository.findByEmailAndIsDeletedFalse(loginUserDTO.getEmail()).orElseThrow(() -> new ApiRequestException("E-mail ou senha incorretos"));

        if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new ApiRequestException("E-mail ou senha incorretos");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Date expiresIn = jwtService.extractExpiration(accessToken);

        return new AuthResponse("success", "Login realizado com sucesso", user, accessToken, refreshToken, expiresIn);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(() -> new ApiRequestException("Refresh token inválido"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new ApiRequestException("Refresh token inválido");
        }

        String accessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        Date expiresIn = jwtService.extractExpiration(accessToken);

        return new AuthResponse("success", "Autenticação atualizada com sucesso", user, accessToken, newRefreshToken, expiresIn);
    }

    public boolean validateToken(String token) {
        try {
            String email = jwtService.extractUsername(token);
            Optional<User> user = userRepository.findByEmailAndIsDeletedFalse(email);
            return user.filter(value -> jwtService.isTokenValid(token, value)).isPresent();
        } catch (ApiRequestException e) {
            return false;
        }
    }
}
