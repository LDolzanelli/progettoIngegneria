package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import it.unibs.ingsw.destinazioni.security.SecurityUtils;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("change-credentials")
@PageTitle("Change Credentials")
@PermitAll
public class ChangeCredentialsView extends VerticalLayout {

    public ChangeCredentialsView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        add(new H1("Update your credentials"));

        TextField newUsername = new TextField("New Username");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");

        Button submit = new Button("Save", event -> {
            String username = newUsername.getValue().trim();
            String password = newPassword.getValue();
            String confirm = confirmPassword.getValue();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Notification.show("All fields are required");
                return;
            }

            if (!password.equals(confirm)) {
                Notification.show("Passwords do not match");
                return;
            }

            String currentUsername = SecurityUtils.getCurrentUsername();

            // Prevent using a username that's already taken
            if (!currentUsername.equals(username) && userRepository.findByNickname(username).isPresent()) {
                Notification.show("Username already in use");
                return;
            }

            userRepository.findByNickname(currentUsername).ifPresentOrElse(user -> {
                user.setNickname(username);
                user.setPassword(passwordEncoder.encode(password)); // or just password if using {noop}
                user.setFirstLogin(false);
                userRepository.save(user);

                Notification.show("Credentials updated successfully. Please log in again.");
                getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")); // Force re-login with new credentials
            }, () -> Notification.show("User not found"));
        });

        add(newUsername, newPassword, confirmPassword, submit);
    }
}
