package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setup() {
        user = new User("test@example.com", "123456","5551981070960","test");
        userRepository.save(user);
    }

    @Test
    void testFindByEmailAndIsDeletedFalse_shouldReturnUser_whenUserExists() {
        Optional<User> foundUser = userRepository.findByEmailAndIsDeletedFalse("test@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testFindByEmailAndIsDeletedFalse_shouldReturnEmpty_whenUserIsDeleted() {
        user.setIsDeleted(true);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmailAndIsDeletedFalse("test@example.com");
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testFindByEmailAndIsDeletedFalse_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> foundUser = userRepository.findByEmailAndIsDeletedFalse("naoexiste@example.com");
        assertThat(foundUser).isEmpty();
    }
}
