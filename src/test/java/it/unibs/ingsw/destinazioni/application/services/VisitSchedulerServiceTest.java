package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VisitSchedulerServiceTest {

    private VisitSchedulerService schedulerService;
    private VisitRepositoryPort visitRepository;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        visitRepository = mock(VisitRepositoryPort.class);
        Clock fixedClock = Clock.fixed(Instant.parse("2025-08-01T00:00:00Z"), ZoneId.systemDefault());
        today = LocalDate.now(fixedClock);

        // Setup di un visitType generico con minimo partecipanti = 1, massimo = 5.
        VisitType visitType = new VisitType("visitType", "description", "meetingPoint", LocalDate.parse("2025-01-01"),
                LocalDate.parse("2026-12-31"), LocalTime.parse("20:00"), 20, 5, 1, true, null, null);
        // setup di visita da manipolare per ogni test case (caso base: visita tra un
        // mese)
        Visit testVisit = new Visit(today.plusDays(30), null, visitType, null, VisitStatus.PROPOSED);

        schedulerService = new VisitSchedulerService(visitRepository, fixedClock);

        // mock comune della repo che ritorna la visit definita
        when(visitRepository.findAll()).thenReturn(Set.of(testVisit));
    }

    @Test
    void updateVisitsStatus_ConfirmedVisit_OldVisitShouldTurnToCompleted() {
        Set<Visit> visits = visitRepository.findAll();

        for (Visit visit : visits) {
            // 10gg prima
            visit.setDate(today.minusDays(10));
            visit.setVisitStatus(VisitStatus.CONFIRMED);

            schedulerService.updateVisitsStatus();

            assertNotNull(visit);
            assertEquals(VisitStatus.COMPLETED, visit.getVisitStatus());
        }
    }

    @Test
    void updateVisitsStatus_ProposedVisitFull_ShouldTurnIntoFull() {
        Set<Visit> visits = visitRepository.findAll();

        for (Visit visit : visits) {
            visit.setVisitStatus(VisitStatus.PROPOSED);

            List<Booking> bookings = new ArrayList<>();
            bookings.add(new Booking("code1", new User("user1", "", Role.FINAL_USER), List.of("visitor1")));
            bookings.add(new Booking("code2", new User("user2", "", Role.FINAL_USER), List.of("visitor2")));
            bookings.add(new Booking("code3", new User("user3", "", Role.FINAL_USER), List.of("visitor3")));
            bookings.add(new Booking("code4", new User("user4", "", Role.FINAL_USER), List.of("visitor4")));
            bookings.add(new Booking("code5", new User("user5", "", Role.FINAL_USER), List.of("visitor5")));

            visit.setBookings(bookings);

            schedulerService.updateVisitsStatus();

            assertNotNull(visit);
            assertEquals(5, visit.visitorsNumber());
            assertEquals(VisitStatus.FULL, visit.getVisitStatus());
        }

    }

    @Test
    void updateVisitsStatus_FullVisitNoLongerFull_ShouldTurnToProposed() {
        Set<Visit> visits = visitRepository.findAll();
        for (Visit visit : visits) {
            visit.setVisitStatus(VisitStatus.FULL);
            List<Booking> bookings = new ArrayList<>();
            bookings.add(new Booking("code1", new User("user1", "", Role.FINAL_USER), List.of("visitor1")));
            bookings.add(new Booking("code2", new User("user2", "", Role.FINAL_USER), List.of("visitor2")));
            bookings.add(new Booking("code3", new User("user3", "", Role.FINAL_USER), List.of("visitor3")));
            bookings.add(new Booking("code4", new User("user4", "", Role.FINAL_USER), List.of("visitor4")));

            visit.setBookings(bookings);

            schedulerService.updateVisitsStatus();

            assertNotNull(visit);
            assertNotEquals(5, visit.visitorsNumber());
            assertEquals(VisitStatus.PROPOSED, visit.getVisitStatus());
        }

    }

    @Test
    void updateVisitsStatus_ThreeDaysBefore_VisitStatusShouldChangeToConfirmedIfMinNumberReached() {
        Set<Visit> visits = visitRepository.findAll();
        for(Visit visit : visits) {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(new Booking("code1", new User("user1", "", Role.FINAL_USER), List.of("visitor1")));

            visit.setBookings(bookings);
            visit.setDate(today.plusDays(2));

            schedulerService.updateVisitsStatus();

            assertNotNull(visit);
            assertEquals(1, visit.visitorsNumber());
            assertEquals(VisitStatus.CONFIRMED, visit.getVisitStatus());
        }
    }

    @Test
    void updateVisitsStatus_ThreeDaysBefore_VisitStatusShouldChangeToCancelledIfMinNumberNotReached() {
        Set<Visit> visits = visitRepository.findAll();

        for (Visit visit : visits) {
            List<Booking> bookings = new ArrayList<>();

            visit.setBookings(bookings);
            visit.setDate(today.plusDays(2));

            schedulerService.updateVisitsStatus();

            assertNotNull(visit);
            assertEquals(0, visit.visitorsNumber());
            assertEquals(VisitStatus.CANCELLED, visit.getVisitStatus());
        }

    }
}
