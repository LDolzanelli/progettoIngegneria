package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import it.unibs.ingsw.destinazioni.domain.model.User;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaUserRepositoryAdapterIT {

    @Autowired
    private JpaUserRepositoryAdapter userAdapter;

    @Test
    void saveAndFindUser_shouldWork() {
        // Arrange
        User user = new User("testUser", "password", "VOLUNTEER");

        // Act
        User saved = userAdapter.save(user);
        Optional<User> found = userAdapter.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getNickname());
        assertEquals("VOLUNTEER", found.get().getRole());
    }

    @Test
    void findByNickname_shouldReturnUser() {
        // Arrange
        User user = new User("uniqueUser", "pass", "ADMIN");
        userAdapter.save(user);

        // Act
        Optional<User> found = userAdapter.findByNickname("uniqueUser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("ADMIN", found.get().getRole());
    }

    @Test
    void findByNonExistingNickname_shouldReturnEmpty() {
        // Act
        Optional<User> found = userAdapter.findByNickname("nonExisting");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void updateUser_shouldWork() {
        // Arrange
        User user = new User("original", "pass", "VOLUNTEER");
        User saved = userAdapter.save(user);
        
        // Act
        saved.setNickname("updated");
        saved.setPassword("newPass");
        saved.setRole("ADMIN");
        User updated = userAdapter.save(saved);
        
        // Assert
        assertEquals("updated", updated.getNickname());
        assertEquals("ADMIN", updated.getRole());
    }
}