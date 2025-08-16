package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VisitSchedulerServiceTest {

    private VisitSchedulerService schedulerService;
    private Visit visit;
    private VisitRepositoryPort visitRepository;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        visitRepository = mock(VisitRepositoryPort.class);
        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-08-01T00:00:00Z"),
                ZoneId.systemDefault()
        );
        today = LocalDate.now(fixedClock);

        //Setup di un visitType generico con minimo partecipanti = 1, massimo = 5.
        VisitType visitType = new VisitType("visitType", "description", "meetingPoint", LocalDate.parse("2025-01-01"),
                LocalDate.parse("2026-12-31"), LocalTime.parse("20:00"), 20, 5,
                1, true, null, null);
        //setup di visita da manipolare per ogni test case (caso base: visita tra un mese)
        visit = new Visit(today.plusDays(30), null, visitType, new HashSet<>(), VisitStatus.PROPOSED);

        schedulerService = new VisitSchedulerService(visitRepository, fixedClock);

        //mock comune della repo che ritorna la visit definita
        when(visitRepository.findAll()).thenReturn(Set.of(visit));
    }

    @Test
    void updateVisitsStatus_checkIfOldVisitTurnsIntoCompletedIfConfirmed() {
        //10gg prima
        visit.setDate(today.minusDays(10));
        visit.setVisitStatus(VisitStatus.CONFIRMED);

        schedulerService.updateVisitsStatus();

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());

        assertEquals(VisitStatus.COMPLETED, captor.getValue().getVisitStatus());
    }

    @Test
    void updateVisitsStatus_checkIfProposedVisitTurnsIntoFull() {
        visit.setVisitStatus(VisitStatus.PROPOSED);
        Set<User> participants = new HashSet<>();
        participants.add(new User("user1", "", Role.FINAL_USER));
        participants.add(new User("user2", "", Role.FINAL_USER));
        participants.add(new User("user3", "", Role.FINAL_USER));
        participants.add(new User("user4", "", Role.FINAL_USER));
        participants.add(new User("user5", "", Role.FINAL_USER));

        visit.setParticipants(participants);

        schedulerService.updateVisitsStatus();

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());

        assertEquals(5, captor.getValue().getParticipants().size());
        assertEquals(VisitStatus.FULL, captor.getValue().getVisitStatus());
    }

    @Test
    void updateVisitsStatus_checkIfFullVisitTurnsIntoProposedWhenNoLongerFull() {
        visit.setVisitStatus(VisitStatus.FULL);
        Set<User> participants = new HashSet<>();
        participants.add(new User("user1", "", Role.FINAL_USER));
        participants.add(new User("user2", "", Role.FINAL_USER));
        participants.add(new User("user3", "", Role.FINAL_USER));
        participants.add(new User("user4", "", Role.FINAL_USER));

        visit.setParticipants(participants);

        schedulerService.updateVisitsStatus();

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());

        assertNotEquals(5, captor.getValue().getParticipants().size());
        assertEquals(VisitStatus.PROPOSED, captor.getValue().getVisitStatus());
    }

    @Test
    void updateVisitsStatus_checkIfVisitStatusChangedThreeDaysBeforeToConfirmedIfMinNumberReached() {
        Set<User> participants = new HashSet<>();
        participants.add(new User("user1", "", Role.FINAL_USER));

        visit.setParticipants(participants);
        visit.setDate(today.plusDays(2));

        schedulerService.updateVisitsStatus();

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());

        assertEquals(1, captor.getValue().getParticipants().size());
        assertEquals(VisitStatus.CONFIRMED, captor.getValue().getVisitStatus());
    }

    @Test
    void updateVisitsStatus_checkIfVisitStatusChangedThreeDaysBeforeToCancelledIfMinNumberNotReached() {
        Set<User> participants = new HashSet<>();

        visit.setParticipants(participants);
        visit.setDate(today.plusDays(2));

        schedulerService.updateVisitsStatus();

        ArgumentCaptor<Visit> captor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(captor.capture());

        assertEquals(0, captor.getValue().getParticipants().size());
        assertEquals(VisitStatus.CANCELLED, captor.getValue().getVisitStatus());
    }
}