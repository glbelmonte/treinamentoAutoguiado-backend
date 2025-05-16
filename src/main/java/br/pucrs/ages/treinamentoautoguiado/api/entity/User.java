package br.pucrs.ages.treinamentoautoguiado.api.entity;

import br.pucrs.ages.treinamentoautoguiado.api.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String cpf;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, name = "is_first_access")
    private Boolean isFirstAccess = true;

	@OneToMany(mappedBy = "user") 
    private List<UserProgress> userProgressList;

    public User(String email, String password, String cpf, String nome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cpf = cpf;
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
