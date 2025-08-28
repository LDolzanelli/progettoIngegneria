package it.unibs.ingsw.destinazioni.config;


import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class AdjustableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public AdjustableClock(Instant initialInstant, ZoneId zone) {
        this.instant = initialInstant;
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new AdjustableClock(this.instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public void plusDays(long days) {
        this.instant = this.instant.plusSeconds(days * 86400);
    }
}
