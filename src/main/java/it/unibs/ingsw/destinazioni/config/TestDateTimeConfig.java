package it.unibs.ingsw.destinazioni.config;

import java.time.Instant;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestDateTimeConfig {

    @Value("${test.clock.start:2025-08-16T00:00:00Z}")
    private String startInstant;

    @Bean
    public AdjustableClock testClock() {
        return new AdjustableClock(
                Instant.parse(startInstant),
                ZoneId.systemDefault()
        );
    }
}

