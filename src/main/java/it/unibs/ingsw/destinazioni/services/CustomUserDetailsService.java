package it.unibs.ingsw.destinazioni.services;

import it.unibs.ingsw.destinazioni.domain.port.UserRepositoryPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import it.unibs.ingsw.destinazioni.domain.model.User;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepository;

    public CustomUserDetailsService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + nickname));

        // Here, the password is stored as plain text (no encoding required)
        return new org.springframework.security.core.userdetails.User(
                user.getNickname(),
                user.getPassword(), // Password is plain text because of {noop}
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
