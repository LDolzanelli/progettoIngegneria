package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.*;
import it.unibs.ingsw.destinazioni.domain.model.*;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitPlanServiceTest {
    private VisitPlanStatePort statePort;
    private VolunteerAvailabilityStatePort availabilityStatePort;
    private VisitTypeRepositoryPort visitTypeRepository;

    private GetUserInfoUseCase userService;
    private VolunteerAvailableDateRepositoryPort volunteerAvailabilityRepository;
    private VisitRepositoryPort visitRepository;

    private VisitPlanService service;

    @BeforeEach
    void setUp() {
        statePort = mock(VisitPlanStatePort.class);
        availabilityStatePort = mock(VolunteerAvailabilityStatePort.class);
        visitTypeRepository = mock(VisitTypeRepositoryPort.class);

        userService = mock(GetUserInfoUseCase.class);
        volunteerAvailabilityRepository = mock(VolunteerAvailableDateRepositoryPort.class);
        visitRepository = mock(VisitRepositoryPort.class);
        BlockedDatesRepositoryPort blockedDatesRepository = mock(BlockedDatesRepositoryPort.class);
        Clock fixedClock = Clock.fixed(Instant.parse("2025-09-01T00:00:00Z"), ZoneId.systemDefault());

        service = new VisitPlanService(statePort, availabilityStatePort, visitTypeRepository,
                fixedClock, userService, volunteerAvailabilityRepository, visitRepository, blockedDatesRepository);

        when(statePort.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(false);
        when(availabilityStatePort.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);
        when(blockedDatesRepository.loadByMonth(anyInt(), anyInt())).thenReturn(new BlockedDates(Set.of()));
    }

    @Test
    void canCreateVisitPlan_ConditionsApply_ShouldReturnTrue() {
        when(statePort.isVisitPlanCreated(anyInt(), anyInt()))
                .thenReturn(false);
        when(availabilityStatePort.isVolunteerAvailabilityOpen(anyInt(), anyInt()))
                .thenReturn(false);

        assertTrue(service.canCreateVisitPlan());
    }

    @Test
    void canCreateVisitPlan_VisitPlanCreated_ShouldReturnFalse() {
        when(statePort.isVisitPlanCreated(anyInt(), anyInt()))
                .thenReturn(true);
        when(availabilityStatePort.isVolunteerAvailabilityOpen(anyInt(), anyInt()))
                .thenReturn(false);

        assertFalse(service.canCreateVisitPlan());
    }

    @Test
    void canCreateVisitPlan_VolunteerAvailabilityOpen_ShouldReturnFalse() {
        when(statePort.isVisitPlanCreated(anyInt(), anyInt()))
                .thenReturn(false);
        when(availabilityStatePort.isVolunteerAvailabilityOpen(anyInt(), anyInt()))
                .thenReturn(true);

        assertFalse(service.canCreateVisitPlan());
    }

    @Test
    void createVisit_VisitPlanCannotBeCreated_ShouldThrowException() {
        when(statePort.isVisitPlanCreated(anyInt(), anyInt()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.createVisitPlan());
    }

    @Test
    void createVisitPlan_NoVolunteersAvailable_ShouldCancelVisit() {
        User user = new User(1, "", "", Role.VOLUNTEER, false);

        when(userService.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(user));
        when(volunteerAvailabilityRepository.findByVolunteerId(1)).thenReturn(List.of());

        VisitType visitTypeMock = mock(VisitType.class);
        Visit visit = new Visit(LocalDate.of(2025, 10, 5), user, //
                visitTypeMock, List.of(), VisitStatus.PROPOSED);

        when(visitRepository.findAll()).thenReturn(Set.of(visit));

        service.createVisitPlan();

        assertEquals(VisitStatus.CANCELLED, visit.getVisitStatus());
        verify(visitRepository).save(visit);
        verify(statePort).setVisitPlanCreated(10, 2025, true);
    }

    @Test
    void createVisitPlan_OneVolunteerAvailableAndCanDoVisit_ShouldAssignVisit() {
        User user = new User(1, "", "", Role.VOLUNTEER, false);
        when(userService.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(user));

        LocalDate date = LocalDate.of(2025, 10, 10);
        VolunteerAvailableDate av = new VolunteerAvailableDate(1, date);
        when(volunteerAvailabilityRepository.findByVolunteerId(1)).thenReturn(List.of(av));

        VisitType visitTypeMock = mock(VisitType.class);
        Visit visit = new Visit(date, null, visitTypeMock, List.of(), VisitStatus.PROPOSED);
        when(visitRepository.findAll()).thenReturn(Set.of(visit));
        when(visitTypeRepository.findByVolunteerId(1)).thenReturn(Set.of(visitTypeMock));

        service.createVisitPlan();

        assertEquals(VisitStatus.PROPOSED, visit.getVisitStatus());
        assertEquals(user, visit.getVolunteer());
        verify(visitRepository).save(visit);
        verify(statePort).setVisitPlanCreated(10, 2025, true);
    }

    @Test
    void createVisitPlan_OneVolunteerAvailableButCannotDoVisit_ShouldCancelVisit() {
        User user = new User(1, "", "", Role.VOLUNTEER, false);
        when(userService.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(user));

        LocalDate date = LocalDate.of(2025, 10, 12);
        VolunteerAvailableDate userAvailableDate = new VolunteerAvailableDate(1, date);
        when(volunteerAvailabilityRepository.findByVolunteerId(1)).thenReturn(List.of(userAvailableDate));

        VisitType visitTypeMock = mock(VisitType.class);
        Visit visit = new Visit(date, null, visitTypeMock, List.of(), VisitStatus.PROPOSED);
        when(visitRepository.findAll()).thenReturn(Set.of(visit));
        when(visitTypeRepository.findByVolunteerId(1)).thenReturn(Set.of());

        service.createVisitPlan();

        assertEquals(VisitStatus.CANCELLED, visit.getVisitStatus());
        verify(visitRepository).save(visit);
        verify(statePort).setVisitPlanCreated(10, 2025, true);
    }

    @Test
    void createVisitPlan_MultipleVolunteersAvailable_ShouldAssignBestVolunteer() {

        User volunteer1 = new User(1, "", "", Role.VOLUNTEER, false);
        User volunteer2 = new User(2, "", "", Role.VOLUNTEER, false);
        when(userService.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(volunteer1, volunteer2));

        LocalDate date = LocalDate.of(2025, 10, 15);
        VolunteerAvailableDate volunteer1AvailableDate = new VolunteerAvailableDate(1, date);
        VolunteerAvailableDate volunteer2AvailableDate = new VolunteerAvailableDate(2, date);
        when(volunteerAvailabilityRepository.findByVolunteerId(1)).thenReturn(List.of(volunteer1AvailableDate));
        when(volunteerAvailabilityRepository.findByVolunteerId(2)).thenReturn(List.of(volunteer2AvailableDate));

        VisitType visitTypeMock = mock(VisitType.class);
        Visit visit = new Visit(date, null, visitTypeMock, List.of(), VisitStatus.PROPOSED);
        when(visitRepository.findAll()).thenReturn(Set.of(visit));
        when(visitTypeRepository.findByVolunteerId(1)).thenReturn(Set.of(visitTypeMock));
        when(visitTypeRepository.findByVolunteerId(2)).thenReturn(Set.of(visitTypeMock));

        service.createVisitPlan();

        assertEquals(VisitStatus.PROPOSED, visit.getVisitStatus());
        assertNotNull(visit.getVolunteer());
        verify(visitRepository).save(visit);
        verify(statePort).setVisitPlanCreated(10, 2025, true);
    }
}