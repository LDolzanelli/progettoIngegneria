package it.unibs.ingsw.destinazioni.service;

import it.unibs.ingsw.destinazioni.entity.User;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUser(String nickname) {
        return userRepository.findByNickname(nickname);
    }
}
