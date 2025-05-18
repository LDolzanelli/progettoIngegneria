package it.unibs.ingsw.destinazioni.services;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort userRepository;

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public User updateNickname(int userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.setNickname(newNickname);
        return userRepository.save(user);
    }

    public User updatePassword(int userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
