package it.unibs.ingsw.destinazioni.application.rest.test;

import java.time.LocalDate;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.config.AdjustableClock;

@RestController
@RequestMapping("api/test/time")
@Profile("test")
public class TestTimeController {

    private final AdjustableClock clock;

    public TestTimeController(AdjustableClock clock) {
        this.clock = clock;
    }

    @PostMapping("/set")
    public void setTime(@RequestParam int day, @RequestParam int month,
                        @RequestParam(required = false) Integer year) {
        int finalYear = (year != null) ? year : LocalDate.now(clock).getYear();
        LocalDate newDate = LocalDate.of(finalYear, month, day);
        clock.setInstant(newDate.atStartOfDay(clock.getZone()).toInstant());
    }

    @GetMapping("/get")
    public LocalDate getTime() {
        return LocalDate.now(clock);
    }
}
