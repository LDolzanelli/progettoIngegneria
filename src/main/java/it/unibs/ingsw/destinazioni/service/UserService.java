package it.unibs.ingsw.destinazioni.service;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity findUser(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public UserEntity updateUser(String nickname, UserEntity user) {
        UserEntity existingUser = findUser(nickname);
//        existingUser.setNickname(nickname);
//
//        return userRepository.save(existingUser);
    }
}
