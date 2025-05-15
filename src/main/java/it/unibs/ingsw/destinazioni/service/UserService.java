package it.unibs.ingsw.destinazioni.service;

import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserEntity> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public UserEntity updateNickname(int userId, String newNickname) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow();
        existingUser.setNickname(newNickname);

        return userRepository.save(existingUser);
    }

    public UserEntity updatePassword(int userId, String newPassword) {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow();
        existingUser.setPassword(newPassword);

        return userRepository.save(existingUser);
    }
}