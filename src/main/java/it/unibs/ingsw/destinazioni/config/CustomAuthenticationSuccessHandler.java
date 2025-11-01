package it.unibs.ingsw.destinazioni.config;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import it.unibs.ingsw.destinazioni.application.services.UserService;
import it.unibs.ingsw.destinazioni.domain.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String nickname = authentication.getName();
        User user = userService.findByNickname(nickname);

        if (Boolean.TRUE.equals(user.isFirstLogin())) {
            response.sendRedirect("/change-credentials?role=" + user.getRole().toString());
        } else {
            response.sendRedirect("/");
        }
    }
}
