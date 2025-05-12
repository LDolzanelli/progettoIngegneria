package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
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

        add(new H1("Aggiorna le credenziali"));
        add(new Paragraph("Al primo accesso, devono essere modificate le credenziali"));

        TextField newUsername = new TextField("Nuovo nome utente");
        PasswordField newPassword = new PasswordField("Nuova Password");
        PasswordField confirmPassword = new PasswordField("Conferma Password");

        Button submit = new Button("Salva", event -> {
            String username = newUsername.getValue().trim();
            String password = newPassword.getValue();
            String confirm = confirmPassword.getValue();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Notification.show("Riempire tutte le caselle");
                return;
            }

            if (!password.equals(confirm)) {
                Notification.show("Le password non corrispondono");
                return;
            }

            String currentUsername = SecurityUtils.getCurrentUsername();

            // Prevent using a username that's already taken
            if (!currentUsername.equals(username) && userRepository.findByNickname(username).isPresent()) {
                Notification.show("Username non disponibile");
                return;
            }

            userRepository.findByNickname(currentUsername).ifPresentOrElse(user -> {
                user.setNickname(username);
                //for now it's not actually encoding.
                //TODO: encode passwords in DB
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstLogin(false);
                userRepository.save(user);

                Notification notification = Notification.show("Credenziali aggiornate con successo. Rieffettuare il login.");
                notification.setDuration(3000);
                notification.setPosition(Notification.Position.MIDDLE);

                //wait 2 seconds before redirecting to the logout page
                getUI().ifPresent(ui ->
                        ui.getPage().executeJs(
                                "setTimeout(() => window.location.replace('/logout'), 2000);"
                        )
                );// Force re-login with new credentials
            }, () -> Notification.show("Utente non trovato"));
        });

        add(newUsername, newPassword, confirmPassword, submit);
    }
}
