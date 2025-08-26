package it.unibs.ingsw.destinazioni.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class DateTimeConfig {
    
    @Bean
    @Profile("!test")
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
    
    @Bean
    @Profile("test")
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2025-08-17T00:00:00Z"), ZoneId.systemDefault());
    }
}