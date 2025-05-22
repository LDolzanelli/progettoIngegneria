package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.Optional;
import it.unibs.ingsw.destinazioni.domain.model.User;


public interface GetUserInfoUseCase {
    Optional<User> findById(int id);
    Optional<User> findByNickname(String nickname);
}
