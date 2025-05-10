package it.unibs.ingsw.destinazioni.repository;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByNickname(String nickname);
}
