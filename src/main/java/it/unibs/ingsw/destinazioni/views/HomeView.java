package it.unibs.ingsw.destinazioni.views;


import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import it.unibs.ingsw.destinazioni.domain.port.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.security.SecurityUtils;
import jakarta.annotation.security.PermitAll;


@Route("")
@PermitAll
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final UserRepositoryPort userRepository;

    public HomeView(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;

        setSpacing(true);
        setPadding(true);

        String username = SecurityUtils.getCurrentUsername();

        add(new H1("Welcome " + username));

    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = SecurityUtils.getCurrentUsername();
        userRepository.findByNickname(username).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.isFirstLogin())) {
                event.forwardTo(ChangeCredentialsView.class);
            }
        });
    }
}
