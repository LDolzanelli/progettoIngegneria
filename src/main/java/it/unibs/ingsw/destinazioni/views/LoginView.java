package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.unibs.ingsw.destinazioni.domain.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final UserRepositoryPort userRepository;

    public LoginView(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var login = new LoginForm();
        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");

        add(
                new H1("Centro Visite"),
                login
        );
        this.userRepository = userRepository;
    }

}