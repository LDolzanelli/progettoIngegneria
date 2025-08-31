package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitSchedulerService {

    private final VisitRepositoryPort visitRepository;

    private final Clock clock;

    // ogni 10 minuti
    @Scheduled(fixedDelay = 600_000)
    @Transactional
    public void updateVisitsStatus() {
        LocalDate today = LocalDate.now(clock);

        Set<Visit> visits = visitRepository.findAll();
        if (visits == null)
            return;

        for (Visit visit : visits) {

            if (visit == null)
                continue;

            LocalDate visitDate = visit.getDate();
            VisitStatus status = visit.getVisitStatus();
            List<?> bookings = visit.getBookings();
            var visitType = visit.getVisitType();

            if (visitDate == null || status == null || /* bookings == null|| */ visitType == null)
                continue;

            if (visitDate.isBefore(today)) {
                handleOldVisitStatus(visit);
            }

            setStatusToFullIfProposedVisitFull(visit);

            setStatusToProposedIfFullVisitNoLongerFull(visit);

            closeVisitStatusThreeDaysBeforeItTakesPlace(visit, today);

            visitRepository.save(visit);
        }
    }

    private void handleOldVisitStatus(Visit visit) {
        if (visit.getVisitStatus() == VisitStatus.CONFIRMED) {
            visit.setVisitStatus(VisitStatus.COMPLETED);
        } else if (visit.getVisitStatus() == VisitStatus.CANCELLED) {
            visitRepository.deleteById(visit.getId());
            System.out.println("visita cancellata");
        }
    }

    public static void setStatusToFullIfProposedVisitFull(Visit visit) {

        if (visit.getVisitStatus() == VisitStatus.PROPOSED &&
                visit.visitorsNumber() == visit.getVisitType().getMaxParticipants()) {
            visit.setVisitStatus(VisitStatus.FULL);
        }
    }

    public static void setStatusToProposedIfFullVisitNoLongerFull(Visit visit) {
        if (visit.getVisitStatus() == VisitStatus.FULL &&
                visit.visitorsNumber() < visit.getVisitType().getMaxParticipants()) {
            visit.setVisitStatus(VisitStatus.PROPOSED);
        }
    }

    private static void closeVisitStatusThreeDaysBeforeItTakesPlace(Visit visit, LocalDate today) {
        long daysUntilVisit = ChronoUnit.DAYS.between(today, visit.getDate());

        if (daysUntilVisit <= 3 && daysUntilVisit > 0) {
            if (visit.visitorsNumber() >= visit.getVisitType().getMinParticipants()) {
                visit.setVisitStatus(VisitStatus.CONFIRMED);
            } else {
                visit.setVisitStatus(VisitStatus.CANCELLED);
            }
        }
    }
}
