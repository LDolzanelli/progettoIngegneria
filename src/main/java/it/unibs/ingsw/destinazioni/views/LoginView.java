package it.unibs.ingsw.destinazioni.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;
import java.security.Security;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private final TextField nickname = new TextField("Nickname");
    private final PasswordField password = new PasswordField("Password");
    private final Button loginBtn = new Button("Login");

    private final RestTemplate restTemplate = new RestTemplate();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginBtn.addClickListener(e -> tryLogin());

        add(new H1("Centro Visite"), nickname, password, loginBtn);
    }


    private void tryLogin() {
        var request = new LoginRequestDTO(nickname.getValue(), password.getValue());

        try {
            LoginResponseDTO response = restTemplate.postForObject("http://localhost:8080/api/users/login", request,
                    LoginResponseDTO.class);

            if (response == null) {
                Notification.show("Errore: risposta vuota dal server");
                return;
            }
            
            

            if (response.firstLogin()) {
                UI.getCurrent().navigate("change-credentials");
            } else {
                UI.getCurrent().navigate("home");
            }

        } catch (HttpClientErrorException e) {
            Notification.show("Login fallito: credenziali errate");
        } catch (Exception e) {
            Notification.show("Errore generico: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
