package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepositoryPort userRepository;
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerNewUser_shouldThrowExceptionIfUsernamePresent() {
        User user = new User("testUser", "", null);
        when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

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
    void changePassword_OldPasswordDoesNotMatch_ShouldThrowException() {
        String password = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(password), Role.CONFIGURATOR);
        when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        Assertions.assertThrows(IllegalArgumentException.class, //
                () -> userService.changePassword("testUser", //
                        "wrongPassword", "test"));
    }

    @Test
    void changePassword_EncodedPasswordsShouldMatch() {
        String oldPassword = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        String newPassword = "newPassword";

        userService.changePassword("testUser", oldPassword, newPassword);

        assertNotNull(user);
        assertNotNull(user.getPassword());
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
    }

    @Test
    void findById_ShouldReturnUser() {
        User user = new User(1, "testUser", "", Role.CONFIGURATOR, false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(1, user.getId());
        assertEquals("testUser", user.getNickname());
    }

    @Test
    void changePassword_AfterChangingPassword_FirstLoginShouldBeFalse() {
        String oldPassword = "testPassword";
        User user = new User("testUser", passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        user.setFirstLogin(true);
        when(userRepository.findByNickname("testUser")).thenReturn(Optional.of(user));

        userService.changePassword("testUser", oldPassword, "newPassword");

        assertNotNull(user);
        assertFalse(user.isFirstLogin());
    }

    @Test
    void changeUsername_ShouldReturnCorrectUsername() {
        String oldUsername = "testUser";
        User user = new User(oldUsername, "test", Role.CONFIGURATOR);
        when(userRepository.findByNickname(oldUsername)).thenReturn(Optional.of(user));

        String newUsername = "newUsername";
        userService.changeUsername(oldUsername, newUsername);

        Assertions.assertNotNull(user);
        assertNotNull(user.getNickname());
        Assertions.assertEquals(newUsername, user.getNickname());
    }

    @Test
    void changeUsername_AfterChangingUsername_FirstLoginShouldBeFalse() {
        String oldUsername = "testUser";
        User user = new User(oldUsername, "test", Role.CONFIGURATOR);
        when(userRepository.findByNickname(oldUsername)).thenReturn(Optional.of(user));
        user.setFirstLogin(true);

        String newUsername = "newUsername";
        userService.changeUsername(oldUsername, newUsername);

        Assertions.assertNotNull(user);
        Assertions.assertFalse(user.isFirstLogin());
    }

    @Test
    void changeBothCredentials_ShouldReturnCorrectUsernameAndPassword() {
        String oldUsername = "testUser";
        String oldPassword = "testPassword";
        User user = new User(oldUsername, passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        when(userRepository.findByNickname(oldUsername)).thenReturn(Optional.of(user));

        String newUsername = "newUsername";
        String newPassword = "newPassword";
        when(userRepository.findByNickname(newUsername)).thenReturn(Optional.of(user));
        userService.changeBothCredentials(oldUsername, newUsername, oldPassword, newPassword);

        Assertions.assertNotNull(user);
        assertNotNull(user.getNickname());
        Assertions.assertEquals(newUsername, user.getNickname());
        assertNotNull(user.getPassword());
        assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
    }

    @Test
    void changeBothCredentials_RoleVolunteerAndUsernameChanged_ShouldThrowException() {
        String oldUsername = "testUser";
        String oldPassword = "testPassword";
        User user = new User(oldUsername, passwordEncoder.encode(oldPassword), Role.VOLUNTEER);
        when(userRepository.findByNickname(oldUsername)).thenReturn(Optional.of(user));

        String newUsername = "newUsername";
        String newPassword = "newPassword";
        when(userRepository.findByNickname(newUsername)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> //
                userService.changeBothCredentials(oldUsername, newUsername, oldPassword, newPassword));
    }

    @Test
    void changeBothCredentials_AfterChangingCredentials_FirstLoginShouldBeFalse() {
        String oldUsername = "testUser";
        String oldPassword = "testPassword";
        User user = new User(oldUsername, passwordEncoder.encode(oldPassword), Role.CONFIGURATOR);
        when(userRepository.findByNickname(oldUsername)).thenReturn(Optional.of(user));
        user.setFirstLogin(true);

        String newUsername = "newUsername";
        String newPassword = "newPassword";

        when(userRepository.findByNickname(newUsername)).thenReturn(Optional.of(user));
        userService.changeBothCredentials(oldUsername, newUsername, oldPassword, newPassword);

        Assertions.assertNotNull(user);
        Assertions.assertFalse(user.isFirstLogin());
    }

    @Test
    void login_UserNotFound_ShouldThrowException() {
        LoginRequestDTO dto = new LoginRequestDTO("testUser", "testPassword");
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.login(dto));
    }

    @Test
    void login_PasswordsDontMatch_ShouldThrowException() {
        LoginRequestDTO dto = new LoginRequestDTO("testUser", "testPassword");
        User user = new User("testUser", "wrongPassword", Role.CONFIGURATOR);
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.login(dto));
    }

    @Test
    void login_ShouldReturnCorrectLoginDetails() {
        LoginRequestDTO dto = new LoginRequestDTO("testUser", "testPassword");
        User user = new User("testUser", passwordEncoder.encode("testPassword"), Role.CONFIGURATOR);
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.of(user));

        LoginResponseDTO loginResponse = userService.login(dto);
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(dto.nickname(), loginResponse.nickname());
        Assertions.assertEquals(Role.CONFIGURATOR.getName(), loginResponse.role());
        Assertions.assertTrue(user.isFirstLogin());
    }
}