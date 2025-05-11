package it.unibs.ingsw.destinazioni.configurator;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import it.unibs.ingsw.destinazioni.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    UserDetailsManager userDetailsManager(){
        return new InMemoryUserDetailsManager(
                User.withUsername("prova")
                        .password("{noop}user")
                        .roles("USER").build()
        );
    }
}