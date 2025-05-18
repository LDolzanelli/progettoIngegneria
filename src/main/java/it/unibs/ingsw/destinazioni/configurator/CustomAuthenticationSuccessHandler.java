package it.unibs.ingsw.destinazioni.configurator;

import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String nickname = authentication.getName();
        User user = userService.findByNickname(nickname).orElseThrow();

        if (Boolean.TRUE.equals(user.isFirstLogin())) {
            response.sendRedirect("/change-credentials");
        } else {
            response.sendRedirect("/");
        }
    }
}
