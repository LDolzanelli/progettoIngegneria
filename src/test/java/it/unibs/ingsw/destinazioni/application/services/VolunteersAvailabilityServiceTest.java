package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VolunteerAvailabilityException;
import it.unibs.ingsw.destinazioni.application.port.in.visit.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailableDateRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.VolunteerAvailableDate;

class VolunteersAvailabilityServiceTest {
    private VolunteerAvailabilityStatePort statePort;
    private VisitPlanStatePort visitPlanStatePort;
    private VisitDaysUseCase visitDaysUseCase;
    private VolunteerAvailableDateRepositoryPort repository;

    private VolunteersAvailabilityService service;

    @BeforeEach
    void setUp() {
        statePort = Mockito.mock(VolunteerAvailabilityStatePort.class);
        visitPlanStatePort = mock(VisitPlanStatePort.class);
        visitDaysUseCase = mock(VisitDaysUseCase.class);
        repository = mock(VolunteerAvailableDateRepositoryPort.class);
    }

    void setUpFixedClockForService(String instantToParse) {
        Clock fixedClock = Clock.fixed(Instant.parse(instantToParse), ZoneId.systemDefault());

        service = new VolunteersAvailabilityService(statePort, visitPlanStatePort, visitDaysUseCase, repository,
                fixedClock);
    }

    @Test
    void canEnableAvailability_NextMonthAvailabilityOpen_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(true);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        assertFalse(service.canEnableAvailability());
    }

    @Test
    void canEnableAvailability_NextMonthVisitPlanNotCreated_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(false);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        assertFalse(service.canEnableAvailability());
    }

    @Test
    void canEnableAvailability_NextTwoMonthsVisitPlanAvailability_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(true);

        assertFalse(service.canEnableAvailability());
    }

    @Test
    void canEnableAvailability_TodayBefore16_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        assertFalse(service.canEnableAvailability());
    }

    @Test
    void canEnableAvailability_AllCorrectConditions_ShouldReturnTrue() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        assertTrue(service.canEnableAvailability());
    }

    @Test
    void canDisableAvailability_TodayBefore16_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(true);

        assertFalse(service.canDisableAvailability());
    }

    @Test
    void canDisableAvailability_NextMonthAvailabilityNotOpen_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);

        assertFalse(service.canDisableAvailability());
    }

    @Test
    void canDisableAvailability_CorrectConditions_ShouldReturnTrue() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(true);

        assertTrue(service.canDisableAvailability());
    }

    @Test
    void enableAvailability_WhenConditionsNotMet_ShouldThrowException() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        assertThrows(VolunteerAvailabilityException.class, () -> service.enableAvailability());
        verify(statePort, never()).setVolunteerAvailabilityOpen(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void enableAvailability_CorrectConditions_ShouldEnableAndCreateVisitDays() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);
        when(visitPlanStatePort.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        service.enableAvailability();

        verify(statePort).setVolunteerAvailabilityOpen(11, 2025, true);
        verify(visitDaysUseCase).createDefaultVisitDays(11);
    }

    @Test
    void disableAvailability_WhenConditionsNotMet_ShouldThrowException() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);

        assertThrows(VolunteerAvailabilityException.class, () -> service.disableAvailability());
        verify(statePort, never()).setVolunteerAvailabilityOpen(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void disableAvailability_CorrectConditions_ShouldDisable() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(true);

        service.disableAvailability();

        verify(statePort).setVolunteerAvailabilityOpen(10, 2025, false);
    }

    @Test
    void getMonthToEnable_ShouldReturnTwoMonthsAhead() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");
        assertEquals(11, service.getMonthToEnable());
    }

    @Test
    void getMonthToDisable_ShouldReturnNextMonth() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");
        assertEquals(10, service.getMonthToDisable());
    }

    @Test
    void isAvailabilityEnabled_Before16_ShouldCheckNextMonth() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(true);

        assertTrue(service.isAvailabilityEnabled());
    }

    @Test
    void isAvailabilityEnabled_OnOrAfter16_ShouldCheckTwoMonthsAhead() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(true);

        assertTrue(service.isAvailabilityEnabled());
    }

    @Test
    void updateAvailability_WhenDisabled_ShouldThrowException() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(10, 2025)).thenReturn(false);

        assertThrows(
                VolunteerAvailabilityException.class,
                () -> service.updateAvailability(1, Set.of(LocalDate.of(2025, 10, 5))));
    }

    @Test
    void updateAvailability_WhenEnabled_ShouldDeleteAndSaveNewDates() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(statePort.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(true);

        VolunteerAvailableDate oldAvailableDate = new VolunteerAvailableDate(1, LocalDate.of(2025, 11, 2));
        when(repository.findByVolunteerId(1)).thenReturn(List.of(oldAvailableDate));

        Set<LocalDate> newAvailableDates = Set.of(LocalDate.of(2025, 11, 3), LocalDate.of(2025, 11, 4));
        service.updateAvailability(1, newAvailableDates);

        verify(repository).delete(oldAvailableDate);
        verify(repository, times(2)).save(any(VolunteerAvailableDate.class));
    }

    @Test
    void getAvailability_ShouldReturnDates() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");

        VolunteerAvailableDate d1 = new VolunteerAvailableDate(1, LocalDate.of(2025, 10, 5));
        VolunteerAvailableDate d2 = new VolunteerAvailableDate(1, LocalDate.of(2025, 10, 6));
        when(repository.findByVolunteerId(1)).thenReturn(List.of(d1, d2));

        Set<LocalDate> result = service.getAvailability(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Set.of(d1.getAvailableDate(), d2.getAvailableDate()), result);
    }

    @Test
    void getAvailability_WithMonthFilter_ShouldReturnOnlyMatching() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");

        VolunteerAvailableDate d1 = new VolunteerAvailableDate(1, LocalDate.of(2025, 10, 5));
        VolunteerAvailableDate d2 = new VolunteerAvailableDate(1, LocalDate.of(2025, 11, 6));
        when(repository.findByVolunteerId(1)).thenReturn(List.of(d1, d2));

        Set<LocalDate> result = service.getAvailability(1, OCTOBER);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Set.of(d1.getAvailableDate()), result);
    }

    @Test
    void getTargetMonth_Before16_ShouldReturnNextMonth() {
        setUpFixedClockForService("2025-09-10T00:00:00Z");

        assertEquals(OCTOBER, service.getTargetMonth());
    }

    @Test
    void getTargetMonth_OnOrAfter16_ShouldReturnMonthPlusTwo() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");

        assertEquals(NOVEMBER, service.getTargetMonth());
    }

}