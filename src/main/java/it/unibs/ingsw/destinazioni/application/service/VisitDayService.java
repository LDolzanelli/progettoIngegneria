package it.unibs.ingsw.destinazioni.application.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class VisitDayService implements VisitDaysUseCase {

    private final VisitRepositoryPort visitRepository;
    private final VisitTypeRepositoryPort visitTypeRepository;
    private final BlockedDatesRepositoryPort blockedDatesRepository;
    private final Clock clock;

    @Override
    public void createDefaultVisitDays(int month) {

        BlockedDates blockedDates = blockedDatesRepository.loadAll();

        LocalDate now = LocalDate.now(clock);
        int year = now.getYear();

        if (month != now.plusMonths(2).getMonthValue() || now.getDayOfMonth() < 15) {
            throw new IllegalArgumentException(
                    "non è possibile creare visite per il mese i + 2 se non è dopo il 15 del mese corrente");
        }

        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        List<VisitType> visitTypes =
                visitTypeRepository.findAll().stream().filter(visitType -> !visitType.getStartDate().isAfter(endOfMonth)
                        && !visitType.getEndDate().isBefore(startOfMonth)).toList();

        for (VisitType visitType : visitTypes) {
            createVisitsForType(visitType, startOfMonth, endOfMonth, blockedDates);
        }
    }

    private void createVisitsForType(VisitType visitType, LocalDate startOfMonth, LocalDate endOfMonth, BlockedDates blockedDates) {
        LocalDate current = startOfMonth;
        while (!current.isAfter(endOfMonth)) {
            if (blockedDates.isBlocked(current)) {
                current = current.plusDays(1);
                continue;
            }
            if (!current.isBefore(visitType.getStartDate()) && !current.isAfter(visitType.getEndDate())) {
                DaysOfWeek day = DaysOfWeek.valueOf(current.getDayOfWeek().name());
                if (visitType.getDaysAvailable().contains(day)) {
                    Visit visit = new Visit(current, null, visitType, Set.of(), VisitStatus.PROPOSED);
                    visitRepository.save(visit);
                }
            }
            current = current.plusDays(1);
        }
    }


    @Override
    public void updateVisitDays(int month) {
        // TODO: implementare la logica per aggiornare i giorni di visita (con il piano di visita)
    }

}
