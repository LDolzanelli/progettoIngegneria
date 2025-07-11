package it.unibs.ingsw.destinazioni.application.service;

import org.springframework.stereotype.Service;
import it.unibs.ingsw.destinazioni.application.port.in.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Clock;
import java.util.Set;



@Service
@RequiredArgsConstructor
public class VisitDayService implements VisitDaysUseCase {

    private final VisitRepositoryPort visitRepository;
    private final VisitTypeRepositoryPort visitTypeRepository;
    private final Clock clock;

    @Override
    public void createDefaultVisitDays(int month) {
        LocalDate now = LocalDate.now(clock);
        int year = now.getYear();

        // Gestione corretta per dicembre ➝ gennaio
        if (month == 1 && now.getMonthValue() == 12) {
            year += 1;
        }

        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        List<VisitType> visitTypes =
                visitTypeRepository.findAll().stream().filter(visitType -> !visitType.getStartDate().isAfter(endOfMonth)
                        && !visitType.getEndDate().isBefore(startOfMonth)).collect(Collectors.toList());

        for (VisitType visitType : visitTypes) {
            LocalDate current = startOfMonth;

            while (!current.isAfter(endOfMonth)) {
                if (!current.isBefore(visitType.getStartDate()) && !current.isAfter(visitType.getEndDate())) {
                    DaysOfWeek day = DaysOfWeek.valueOf(current.getDayOfWeek().name());

                    if (visitType.getDaysAvailable().contains(day)) {
                        Visit visit = new Visit(current, null, "PROPOSED", visitType, Set.of(), 
                                VisitStatus.PROPOSED);

                        visitRepository.save(visit);
                    }
                }
                current = current.plusDays(1);
            }
        }
    }


    @Override
    public void updateVisitDays(int month) {
        // TODO: implementare la logica per aggiornare i giorni di visita (con il piano di visita)
    }



}
