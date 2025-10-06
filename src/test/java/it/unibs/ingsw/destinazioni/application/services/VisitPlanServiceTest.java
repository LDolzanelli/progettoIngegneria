package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.*;
import it.unibs.ingsw.destinazioni.domain.model.*;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitPlanServiceTest {
    private VisitPlanStatePort statePort;
    private VolunteerAvailabilityStatePort availabilityStatePort;
    private VisitTypeRepositoryPort visitTypeRepository;

    private GetUserInfoUseCase userInfoUseCase;
    private VolunteerAvailableDateRepositoryPort volunteerAvailabilityRepository;
    private VisitRepositoryPort visitRepository;

    private VisitPlanService service;

    @BeforeEach
    void setUp() {
        statePort = mock(VisitPlanStatePort.class);
        availabilityStatePort = mock(VolunteerAvailabilityStatePort.class);
        visitTypeRepository = mock(VisitTypeRepositoryPort.class);

        userInfoUseCase = mock(GetUserInfoUseCase.class);
        volunteerAvailabilityRepository = mock(VolunteerAvailableDateRepositoryPort.class);
        visitRepository = mock(VisitRepositoryPort.class);
        BlockedDatesRepositoryPort blockedDatesRepository = mock(BlockedDatesRepositoryPort.class);
        Clock fixedClock = Clock.fixed(Instant.parse("2025-09-01T00:00:00Z"), ZoneId.systemDefault());

        service = new VisitPlanService(statePort, availabilityStatePort, visitTypeRepository,
                fixedClock, userInfoUseCase, volunteerAvailabilityRepository, visitRepository, blockedDatesRepository);

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

        when(userInfoUseCase.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(user));
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
        User volunteer = new User(1, "volunteer", "test", Role.VOLUNTEER, false);
        when(userInfoUseCase.getUsersIdsByRole(Role.VOLUNTEER)).thenReturn(List.of(volunteer.getId()));
        when(userInfoUseCase.findById(1)).thenReturn(Optional.of(volunteer));

        LocalDate date = LocalDate.of(2025, 10, 10);
        VolunteerAvailableDate availableDate = new VolunteerAvailableDate(1, date);

        VisitType visitTypeMock = mock(VisitType.class);
        Visit visit = new Visit(date, null, visitTypeMock, List.of(), VisitStatus.PROPOSED);
        when(visitRepository.findAll()).thenReturn(Set.of(visit));
        when(visitTypeRepository.findByVolunteerId(1)).thenReturn(Set.of(visitTypeMock));

        List<VolunteerAvailableDate> volunteerAvailableDates = new ArrayList<>();
        volunteerAvailableDates.add(availableDate);

        when(volunteerAvailabilityRepository.findByYearMonth(YearMonth.of(2025, 10))).thenReturn(volunteerAvailableDates);

        service.createVisitPlan();

        assertEquals(VisitStatus.PROPOSED, visit.getVisitStatus());
        assertEquals(1, visit.getVolunteer().getId());
        verify(visitRepository).save(visit);
        verify(statePort).setVisitPlanCreated(10, 2025, true);
    }

    @Test
    void createVisitPlan_OneVolunteerAvailableButCannotDoVisit_ShouldCancelVisit() {
        User user = new User(1, "", "", Role.VOLUNTEER, false);
        when(userInfoUseCase.getUsersByRole(Role.VOLUNTEER)).thenReturn(List.of(user));

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
        when(userInfoUseCase.getUsersIdsByRole(Role.VOLUNTEER)).thenReturn(List.of(volunteer1.getId(), volunteer2.getId()));
        when(userInfoUseCase.findById(1)).thenReturn(Optional.of(volunteer1));
        when(userInfoUseCase.findById(2)).thenReturn(Optional.of(volunteer2));

        LocalDate date = LocalDate.of(2025, 10, 15);
        VolunteerAvailableDate volunteer1AvailableDate = new VolunteerAvailableDate(1, date);
        VolunteerAvailableDate volunteer2AvailableDate = new VolunteerAvailableDate(2, date);
        when(volunteerAvailabilityRepository.findByVolunteerId(1)).thenReturn(List.of(volunteer1AvailableDate));
        when(volunteerAvailabilityRepository.findByVolunteerId(2)).thenReturn(List.of(volunteer2AvailableDate));

        List<VolunteerAvailableDate> volunteerAvailableDates = new ArrayList<>();
        volunteerAvailableDates.add(volunteer1AvailableDate);
        volunteerAvailableDates.add(volunteer2AvailableDate);

        when(volunteerAvailabilityRepository.findByYearMonth(YearMonth.of(2025, 10))).thenReturn(volunteerAvailableDates);

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