package br.pucrs.ages.treinamentoautoguiado.api.repository;

import br.pucrs.ages.treinamentoautoguiado.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);

}
