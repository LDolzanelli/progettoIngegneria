package it.unibs.ingsw.destinazioni.repository;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByNickname(String nickname);
}
