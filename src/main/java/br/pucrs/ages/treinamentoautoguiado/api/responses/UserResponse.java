package br.pucrs.ages.treinamentoautoguiado.api.responses;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {

    private Long id;
    private String email;
    private String nome;
    private String cpf;

    @JsonProperty(value = "modified_by")
    private String modifiedBy;

    @JsonProperty(value = "deleted_at")
    private LocalDateTime deletedAt;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty(value = "updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty(value = "is_first_access")
    private Boolean isFirstAccess;

    private List<String> roles;

    @JsonProperty(value = "is_deleted")
    private boolean isDeleted;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.roles = List.of(user.getRole().name());
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.modifiedBy = user.getModifiedBy();
        this.isDeleted = user.getIsDeleted();
        this.deletedAt = user.getDeletedAt();
        this.cpf = user.getCpf();
        this.nome = user.getNome();
        this.isFirstAccess = user.getIsFirstAccess();
    }
}
