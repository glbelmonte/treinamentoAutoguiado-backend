package br.pucrs.ages.treinamentoautoguiado.api.responses;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@Getter
public class AuthResponse {

    private String status;
    private AuthResponseData data;
    private LocalDateTime timestamp;

    public AuthResponse(String status, String message, User user, String accessToken, String refreshToken, Date expiresIn) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.data = new AuthResponseData(user, accessToken, refreshToken, expiresIn, message);
    }

}

@Data
@Getter
class AuthResponseData {

    private UserResponse user;
    private TokenResponse token;
    private String message;

    public AuthResponseData(User user, String accessToken, String refreshToken, Date expiresIn, String message) {
        this.user = new UserResponse(user);
        this.token = new TokenResponse(accessToken, refreshToken, expiresIn.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        this.message = message;
    }
}