package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.unibs.ingsw.destinazioni.entity.UserEntity;
import it.unibs.ingsw.destinazioni.repository.UserRepository;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final UserRepository userRepository;

    public LoginView(UserRepository userRepository) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        for(UserEntity u : userRepository.findAll()) {
            add(new Paragraph(u.getId() + " " + u.getNickname() + " " + u.getPassword()));
        }

        var login = new LoginForm();
        login.setAction("login");

        add(
                new H1("Centro Visite"),
                login
        );
        this.userRepository = userRepository;
    }
}