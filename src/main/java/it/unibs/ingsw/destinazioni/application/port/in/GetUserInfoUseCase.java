package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;
import java.util.Optional;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;


public interface GetUserInfoUseCase {
    Optional<User> findById(int id);
    Optional<User> findByNickname(String nickname);
    List<User> getUsersByRole(Role role);
    List<User> findAllByNicknames(List<String> nicknames);
}
