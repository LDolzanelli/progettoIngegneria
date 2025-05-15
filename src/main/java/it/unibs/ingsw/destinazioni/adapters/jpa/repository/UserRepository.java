package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByNickname(String nickname);
}
