package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VisitSchedulerService {

    private final VisitRepositoryPort visitRepository;

    private final Clock clock;

    //ogni 10 minuti
    @Scheduled(fixedDelay = 600_000)
    @Transactional
    public void updateVisitsStatus() {
        LocalDate today = LocalDate.now(clock);

        Set<Visit> visits = visitRepository.findAll();

        for (Visit visit : visits) {
            LocalDate visitDate = visit.getDate();
            VisitStatus status = visit.getVisitStatus();

            if (checkIfVisitIsBeforeToday(visit, visitDate, today, status))
                continue;

            setStatusToFullIfProposedVisitFull(visit, status);

            setStatusToProposedIfFullVisitNoLongerFull(visit, status);

            closeVisitStatusThreeDaysBeforeTakesPlace(visit, today, visitDate);

            visitRepository.save(visit);
        }
    }

    private boolean checkIfVisitIsBeforeToday(Visit visit, LocalDate visitDate, LocalDate today, VisitStatus status)
    {
        if (visitDate.isBefore(today)) {
            if (status == VisitStatus.CONFIRMED) {
                visit.setVisitStatus(VisitStatus.COMPLETED);
            } else if (status == VisitStatus.CANCELLED) {
                //visitRepository.delete(visit);
                return true;
            }
        }
        return false;
    }

    private static void setStatusToFullIfProposedVisitFull(Visit visit, VisitStatus status)
    {
        if (status == VisitStatus.PROPOSED &&
                visit.getParticipants().size() == visit.getVisitType().getMaxParticipants()) {
            visit.setVisitStatus(VisitStatus.FULL);
        }
    }

    private static void setStatusToProposedIfFullVisitNoLongerFull(Visit visit, VisitStatus status)
    {
        if (status == VisitStatus.FULL &&
                visit.getParticipants().size() < visit.getVisitType().getMaxParticipants()) {
            visit.setVisitStatus(VisitStatus.PROPOSED);
        }
    }

    private static void closeVisitStatusThreeDaysBeforeTakesPlace(Visit visit, LocalDate today, LocalDate visitDate)
    {
        long daysUntilVisit = ChronoUnit.DAYS.between(today, visitDate);

        if (daysUntilVisit == 3) {
            if (visit.getParticipants().size() >= visit.getVisitType().getMinParticipants()) {
                visit.setVisitStatus(VisitStatus.CONFIRMED);
            } else {
                visit.setVisitStatus(VisitStatus.CANCELLED);
            }
        }
    }
}
