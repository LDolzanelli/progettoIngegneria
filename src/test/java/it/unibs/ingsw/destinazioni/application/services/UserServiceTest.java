package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserRepositoryPort userRepository;
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepositoryPort.class);
        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerNewUser_shouldThrowExceptionIfUsernamePresent() {
        User user = new User("testUser", "", null);
        Mockito.when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser(user));
    }

    @Test
    void registerNewUser_RoleFinalUser_ShouldSetFirstLoginToFalse() {
        User user = new User("testUser", "", Role.FINAL_USER);

        userService.registerNewUser(user);

        assertNotNull(user);
        assertNotNull(user.getRole());
        assertFalse(user.isFirstLogin());
    }

    @Test
    void registerNewUser_RoleNotFinalUser_ShouldSetFirstLoginToTrue() {
        User user1 = new User("testUser1", "", Role.VOLUNTEER);
        User user2 = new User("testUser2", "", Role.CONFIGURATOR);

        //il service dovrebbe forzare i valori a true
        user1.setFirstLogin(false);
        user2.setFirstLogin(false);

        userService.registerNewUser(user1);
        userService.registerNewUser(user2);

        assertNotNull(user1);
        assertNotNull(user1.getRole());
        assertTrue(user1.isFirstLogin());

        assertNotNull(user2);
        assertNotNull(user2.getRole());
        assertTrue(user2.isFirstLogin());
    }

    @Test
    void registerNewUser_ShouldEncodePassword() {
        String password = "testPassword";
        User user = new User("testUser", password, Role.CONFIGURATOR);

        userService.registerNewUser(user);

        assertNotNull(user);
        assertNotNull(user.getPassword());
        assertNotEquals(password, user.getPassword());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
    }

    @Test
    void changePassword_ShouldThrowExceptionIfOldPasswordDoesNotMatch() {
        String password = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(password), Role.CONFIGURATOR);
        Mockito.when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalArgumentException.class, //
                () -> userService.changePassword("testUser", //
                        "wrongPassword", "test"));
    }

    @Test
    void changePassword_EncodedPasswordsShouldMatch() {
        String oldPassword = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        Mockito.when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        String newPassword = "newPassword";

        userService.changePassword("testUser", oldPassword, newPassword);

        assertNotNull(user);
        assertNotNull(user.getPassword());
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
    }

    @Test
    void changePassword_FirstLoginShouldBeFalseAfterChangingPassword() {
        String oldPassword = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        user.setFirstLogin(true);
        Mockito.when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        userService.changePassword("testUser", oldPassword, "newPassword");

        assertNotNull(user);
        assertFalse(user.isFirstLogin());
    }

    @Test
    void changeUsername() {
    }

    @Test
    void changeBothCredentials() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByNickname() {
    }

    @Test
    void login() {
    }

    @Test
    void getUsersByRole() {
    }

    @Test
    void findAllByNicknames() {
    }

    @Test
    void getIdByNickname() {
    }
}