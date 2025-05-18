package it.unibs.ingsw.destinazioni.configurator;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import it.unibs.ingsw.destinazioni.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // ❌ Disattiva CSRF solo per le API
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
        );

        // ✅ Consenti accesso libero alle API
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
        );

        // ⚙️ Configurazione Vaadin
        super.configure(http);
        setLoginView(http, LoginView.class);

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
