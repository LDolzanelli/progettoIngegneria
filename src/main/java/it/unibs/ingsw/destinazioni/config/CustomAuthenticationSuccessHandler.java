package it.unibs.ingsw.destinazioni.config;

import it.unibs.ingsw.destinazioni.application.service.UserService;
import it.unibs.ingsw.destinazioni.domain.model.User;
import org.springframework.context.annotation.Lazy;
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

    public CustomAuthenticationSuccessHandler(@Lazy UserService userService) {
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
            response.sendRedirect("/change-credentials?role=" + user.getRole().toString());
        } else {
            response.sendRedirect("/");
        }
    }
}
