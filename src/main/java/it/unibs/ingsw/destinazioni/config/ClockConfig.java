package it.unibs.ingsw.destinazioni.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    @Bean
    @Profile("!test")
    @Primary
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @Profile("test")
    @Primary
    public AdjustableClock adjustableClock() {
        LocalDate fixedDate = LocalDate.of(2025, 8, 16);
        Instant fixedInstant = fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return new AdjustableClock(fixedInstant, ZoneId.systemDefault());
    }
}