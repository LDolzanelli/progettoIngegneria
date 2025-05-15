package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

@Route("logout")
@PermitAll
public class LogoutView extends Main implements AfterNavigationObserver {

    private final AuthenticationContext authenticationContext;

    public LogoutView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        authenticationContext.logout();
    }
}
