package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.VolunteersAvailabilityUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteersAvailabilityService implements VolunteersAvailabilityUseCase {

    private final VolunteerAvailableDateRepositoryPort repository;
    private final Clock clock;

    @Override
    public void updateAvailability(int volunteerId, Set<LocalDate> availableDates) {
        Month target = getTargetMonth();

        //cancella e rimpiazza con il nuovo set di date, per garantire un update completo
        repository.findByVolunteerId(volunteerId).stream()
                  .filter(d -> d.getAvailableDate().getMonth() == target)
                  .forEach(repository::delete);

        availableDates.forEach(d ->
                repository.save(new VolunteerAvailableDate(volunteerId, d)));
    }

    @Override
    public Set<LocalDate> getAvailability(int volunteerId) {
        return repository.findByVolunteerId(volunteerId)
                         .stream()
                         .map(VolunteerAvailableDate::getAvailableDate)
                         .collect(Collectors.toSet());
    }

    @Override
    public Set<LocalDate> getAvailability(int volunteerId, Month month) {
        return repository.findByVolunteerId(volunteerId)
                         .stream()
                         .filter(v -> v.getAvailableDate().getMonth() == month)
                         .map(VolunteerAvailableDate::getAvailableDate)
                         .collect(Collectors.toSet());
    }

    @Override
    public Month getTargetMonth() {
        LocalDate today = LocalDate.now(clock);
        int base = today.getDayOfMonth() < 16 ? today.getMonthValue()
                                              : today.plusMonths(1).getMonthValue();
        int target = (base % 12) + 1;
        return Month.of(target);
    }
}
