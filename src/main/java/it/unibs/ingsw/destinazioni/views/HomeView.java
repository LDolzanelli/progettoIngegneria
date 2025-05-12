package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import it.unibs.ingsw.destinazioni.repository.UserRepository;
import it.unibs.ingsw.destinazioni.security.SecurityUtils;
import jakarta.annotation.security.PermitAll;


@Route("")
@PermitAll
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    private final UserRepository userRepository;

    public HomeView(UserRepository userRepository) {
        this.userRepository = userRepository;
        add(new H1("Welcome to your new application"));
        add(new Paragraph("This is the home view"));
        add(new Paragraph(SecurityUtils.getCurrentUsername()));
        add(new Paragraph("You can edit this view in src\\main\\java\\it\\unibs\\ingsw\\destinazioni\\views\\HomeView.java"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = SecurityUtils.getCurrentUsername();
        userRepository.findByNickname(username).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getFirstLogin())) {
                event.forwardTo(ChangeCredentialsView.class);
            }
        });
    }

}
