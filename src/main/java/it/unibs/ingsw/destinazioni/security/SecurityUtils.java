package it.unibs.ingsw.destinazioni.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.vaadin.flow.server.VaadinSession;

public class SecurityUtils {

    public static String getCurrentUsername() {
        // fallback se non autenticato da Spring
        String vaadinNickname = (String) VaadinSession.getCurrent().getAttribute("nickname");
        if (vaadinNickname != null)
            return vaadinNickname;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails)
                return ((UserDetails) principal).getUsername();
            else if (principal instanceof String)
                return (String) principal;
        }

        return null;
    }
}
