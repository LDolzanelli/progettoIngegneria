package it.unibs.ingsw.destinazioni.domain.port;

import it.unibs.ingsw.destinazioni.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(int id);
    Optional<User> findByNickname(String nickname);
    User save(User user);
    void deleteByNickname(String nickname);
    void deleteById(int id);
}
