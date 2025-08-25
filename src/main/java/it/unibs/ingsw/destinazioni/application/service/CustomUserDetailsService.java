package it.unibs.ingsw.destinazioni.application.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
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

        return new org.springframework.security.core.userdetails.User(
                user.getNickname(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
