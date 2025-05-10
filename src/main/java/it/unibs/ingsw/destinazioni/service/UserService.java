package it.unibs.ingsw.destinazioni.service;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public UserEntity updateNickname(int userId, String newNickname) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow();
        existingUser.setNickname(newNickname);

        return userRepository.save(existingUser);
    }
}
