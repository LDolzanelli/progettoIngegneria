package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitDayException;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BlockedDatesRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.BlockedDates;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;

class VisitDayServiceTest {

    private VisitRepositoryPort visitRepositoryMock;
    private VisitTypeRepositoryPort visitTypeRepositoryMock;
    private BlockedDatesRepositoryPort blockedDatesRepositoryMock;
    private GetUserInfoUseCase userInfoServiceMock;

    private VisitDayService service;

    @BeforeEach
    void setUp() {
        visitRepositoryMock = mock(VisitRepositoryPort.class);
        visitTypeRepositoryMock = mock(VisitTypeRepositoryPort.class);
        blockedDatesRepositoryMock = mock(BlockedDatesRepositoryPort.class);
        userInfoServiceMock = mock(GetUserInfoUseCase.class);

        BlockedDates blockedDates = mock(BlockedDates.class);
        when(blockedDatesRepositoryMock.loadAll()).thenReturn(blockedDates);
    }


    @Test
    void createDefaultVisitDays_DateNotTwoMonthsFromNow_ShouldThrowException() {
        setUpClockForService("2025-08-16T00:00:00Z");

        Set<LocalDate> dates = Set.of(LocalDate.of(2025, 10, 10));
        BlockedDates blockedDates = new BlockedDates(dates);

        when(blockedDatesRepositoryMock.loadAll()).thenReturn(blockedDates);

        assertThrows(VisitDayException.class, () -> service.createDefaultVisitDays(8));
    }


    @Test
    void createDefaultVisitDays_TodayDateBefore15_ShouldThrowException() {
        setUpClockForService("2025-08-10T00:00:00Z");

        assertThrows(VisitDayException.class, () -> service.createDefaultVisitDays(10));
    }


    @Test
    void createDefaultVisitDays_ShouldReturnVisitDaysAsExpected() {
        int blockedDateDay = 10;
        int startDateDay = 1;
        int endDateDay = 15;
        setUpCreateDeafultVisitDays(blockedDateDay, startDateDay, endDateDay);

        verify(visitRepositoryMock, atLeastOnce()).save(any(Visit.class));
    }


    @Test
    void createDefaultVisitDays_ShouldIgnoreBlockedDates() {
        int blockedDateDay = 10;
        int startDateDay = blockedDateDay;
        int endDateDay = blockedDateDay;
        setUpCreateDeafultVisitDays(blockedDateDay, startDateDay, endDateDay);

        verify(visitRepositoryMock, never()).save(any(Visit.class));
    }


    @Test
    void getConfirmedVisitsPerVolunteer_UserNotVolunteer_ShouldThrowException() {
        setUpClockForService("2025-08-10T00:00:00Z");
        User user = new User("test", "test", Role.CONFIGURATOR);
        when(userInfoServiceMock.findByNickname("test")).thenReturn(user);

        assertThrows(
                UserException.class,
                () -> service.getConfirmedVisitsPerVolunteer(user.getNickname()));
    }


    @Test
    void getConfirmedVisitsPerVolunteer_ShouldReturnExpectedVisitsList() {
        setUpClockForService("2025-08-10T00:00:00Z");
        User user = new User("test", "test", Role.VOLUNTEER);
        when(userInfoServiceMock.findByNickname("test")).thenReturn(user);

        Visit visitConfirmed = new Visit(null, user, null, List.of(), VisitStatus.CONFIRMED);
        Visit visitNotConfirmed = new Visit(null, user, null, List.of(), VisitStatus.PROPOSED);

        Set<Visit> setOfVisits = Set.of(visitConfirmed, visitNotConfirmed);
        when(visitRepositoryMock.findByVolunteer(user.getNickname())).thenReturn(setOfVisits);
        List<Visit> confirmedVisits = service.getConfirmedVisitsPerVolunteer(user.getNickname());

        assertNotNull(confirmedVisits);
        assertEquals(1, confirmedVisits.size());
        assertTrue(confirmedVisits.contains(visitConfirmed));
        assertFalse(confirmedVisits.contains(visitNotConfirmed));
    }


    void setUpClockForService(String instantToParse) {
        Clock fixedClock = Clock.fixed(Instant.parse(instantToParse), ZoneId.systemDefault());

        service = new VisitDayService(visitRepositoryMock, visitTypeRepositoryMock, //
                blockedDatesRepositoryMock, userInfoServiceMock, fixedClock);
    }


    private void setUpCreateDeafultVisitDays(int dayOfBlockedDate, int startDateDay, int endDateDay) {
        setUpClockForService("2025-08-16T00:00:00Z");
        int targetMonth = 10;

        Set<LocalDate> dates = Set.of(LocalDate.of(2025, targetMonth, dayOfBlockedDate));
        BlockedDates blockedDates = new BlockedDates(dates);

        when(blockedDatesRepositoryMock.loadAll()).thenReturn(blockedDates);

        List<DaysOfWeek> days = List.of(DaysOfWeek.FRIDAY);
        VisitType visitType = new VisitType("", "", "", LocalDate.of(2025, targetMonth, startDateDay),
                LocalDate.of(2025, targetMonth, endDateDay), null, //
                1, 1, 10, true, days, List.of());
        Set<VisitType> listVisits = Set.of(visitType);

        when(visitTypeRepositoryMock.findAll()).thenReturn(listVisits);
        service.createDefaultVisitDays(targetMonth);
    }

}
