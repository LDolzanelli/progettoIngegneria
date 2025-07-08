package it.unibs.ingsw.destinazioni.application.port.out;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(int id);
    Optional<User> findByNickname(String nickname);
    User save(User user);
    void deleteByNickname(String nickname);
    void deleteById(int id);
    List<User> findAllByRole(Role role);
}
