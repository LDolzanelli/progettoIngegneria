package it.unibs.ingsw.destinazioni.application.services;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.VisitTypeException;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitPlanStatePort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitTypeRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VolunteerAvailabilityStatePort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

class VisitTypeServiceTest {

    private VisitTypeRepositoryPort repository;
    private LocationRepositoryPort locationRepository;
    private UserRepositoryPort userRepository;
    private VolunteerAvailabilityStatePort volunteerAvailabilityStateRepo;
    private VisitPlanStatePort visitPlanStateRepo;

    private VisitTypeService service;

    @BeforeEach
    void setUp() {
        repository = mock(VisitTypeRepositoryPort.class);
        locationRepository = mock(LocationRepositoryPort.class);
        userRepository = mock(UserRepositoryPort.class);
        volunteerAvailabilityStateRepo = mock(VolunteerAvailabilityStatePort.class);
        visitPlanStateRepo = mock(VisitPlanStatePort.class);

    }

    void setUpFixedClockForService(String instantToParse) {
        Clock fixedClock = Clock.fixed(Instant.parse(instantToParse), ZoneId.systemDefault());

        service = new VisitTypeService(repository, locationRepository, userRepository, //
                volunteerAvailabilityStateRepo, visitPlanStateRepo, fixedClock);
    }

    @Test
    void removeVisitType_CorrectDate_RemovesLocationAndVolunteer() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");

        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));
        User user = new User(10, "user", "password", null, false);
        when(visitType.getVolunteers()).thenReturn(List.of(user));

        Location location = mock(Location.class);
        when(location.getId()).thenReturn(100);
        when(location.getVisitTypes()).thenReturn(List.of(visitType));

        when(repository.findById(1)).thenReturn(Optional.of(visitType));
        when(locationRepository.findByVisitType(visitType)).thenReturn(Optional.of(location));
        when(repository.findByVolunteerId(10)).thenReturn(Set.of());
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(11, 2025)).thenReturn(false);

        service.removeVisitType(1);

        verify(locationRepository).save(any(Location.class));
        verify(userRepository).deleteById(10);
    }

    @Test
    void removeVisitType_WrongDate_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");

        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 10, 1));
        when(visitType.getId()).thenReturn(1);

        when(repository.findById(1)).thenReturn(Optional.of(visitType));
        assertThrows(VisitTypeException.class, () -> service.removeVisitType(2));
    }

    @Test
    void updateVisitType_NullVisitType_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = null;
        assertThrows(VisitTypeException.class, () -> service.updateVisitType(visitType));
    }

    @Test
    void updateVisitType_NullVisitTypeId_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        assertThrows(VisitTypeException.class, () -> service.updateVisitType(visitType));
    }

    @Test
    void updateVisitType_NullExistingVisit_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);

        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(VisitTypeException.class, () -> service.updateVisitType(visitType));
    }

    @Test
    void updateVisitType_NullLocation_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));
        when(locationRepository.findByVisitType(visitType)).thenReturn(Optional.empty());
        assertThrows(VisitTypeException.class, () -> service.updateVisitType(visitType));
    }

    @Test
    void updateVisitType_ShouldSaveAsExpected() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(2);
        when(locationRepository.findByVisitType(visitType)).thenReturn(Optional.of(location));
        service.updateVisitType(visitType);

        verify(repository).save(eq(visitType), eq(2));
    }

    @Test
    void isAddOrRemovalStateActive_Before15_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        assertFalse(service.isAddOrRemovalStateActive());
    }

    @Test
    void isAddOrRemovalStateActive_VisitPlanNotCreated_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(false);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        assertFalse(service.isAddOrRemovalStateActive());
    }

    @Test
    void isAddOrRemovalStateActive_VolunteerAvailabilityOpen_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(true);

        assertFalse(service.isAddOrRemovalStateActive());
    }

    @Test
    void isAddOrRemovalStateActive_CorrectConditions_ShouldReturnTrue() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        assertTrue(service.isAddOrRemovalStateActive());
    }

    @Test
    void canBeRemoved_VisitTypeNotFound_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(VisitTypeException.class, () -> service.canBeRemoved(1));
    }

    @Test
    void canBeRemoved_StartDateWithinTwoMonths_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        VisitType visitType = mock(VisitType.class);

        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 11, 1));
        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertFalse(service.canBeRemoved(1));
    }

    @Test
    void canBeRemoved_StartDateAfterMoreThanTwoMonths_ShouldReturnTrue() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(visitPlanStateRepo.isVisitPlanCreated(anyInt(), anyInt())).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        VisitType visitType = mock(VisitType.class);

        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));
        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertTrue(service.canBeRemoved(1));
    }

    @Test
    void canBeModified_VisitTypeNotFound_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(VisitTypeException.class, () -> service.canBeModified(1));
    }

    @Test
    void canBeModified_CannotBeRemoved_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(false);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(true);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertFalse(service.canBeModified(1));
    }

    @Test
    void canBeModified_VisitDateMonthVisitPlanCreated_ShouldReturnFalse() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(true);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertFalse(service.canBeModified(1));
    }

    @Test
    void canBeModified_CorrectConditions_ShouldReturnTrue() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(false);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertTrue(service.canBeModified(1));
    }

    @Test
    void addVolunteerToVisitType_VisitTypeCannotBeModified_ShouldThrowException() {
        setUpFixedClockForService("2025-09-14T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(true);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        assertThrows(VisitTypeException.class, () -> service.addVolunteerToVisitType(1, ""));
    }

    @Test
    void addVolunteerToVisitType_VolunteerAlreadyPresent_ShouldThrowException() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(false);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        String volunteerName = "volunteer";
        User volunteer = new User(volunteerName, "password", Role.VOLUNTEER);

        when(userRepository.findByNickname(volunteerName)).thenReturn(Optional.of(volunteer));
        when(visitType.getVolunteers()).thenReturn(List.of(volunteer));

        assertThrows(VisitTypeException.class, () -> service.addVolunteerToVisitType(1, volunteerName));
    }

    @Test
    void addVolunteerToVisitType_VolunteerNotPresent_ShouldAddToVisitType() {
        setUpFixedClockForService("2025-09-16T00:00:00Z");
        VisitType visitType = mock(VisitType.class);
        when(visitType.getStartDate()).thenReturn(LocalDate.of(2025, 12, 1));

        when(visitPlanStateRepo.isVisitPlanCreated(12, 2025)).thenReturn(false);
        when(visitPlanStateRepo.isVisitPlanCreated(10, 2025)).thenReturn(true);
        when(volunteerAvailabilityStateRepo.isVolunteerAvailabilityOpen(anyInt(), anyInt())).thenReturn(false);

        when(repository.findById(anyInt())).thenReturn(Optional.of(visitType));

        String volunteerName = "volunteer";
        User volunteer = new User(volunteerName, "password", Role.VOLUNTEER);

        when(userRepository.findByNickname(volunteerName)).thenReturn(Optional.of(volunteer));
        when(visitType.getVolunteers()).thenReturn(List.of());

        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1);
        when(locationRepository.findByVisitType(visitType)).thenReturn(Optional.of(location));

        service.addVolunteerToVisitType(1, volunteerName);

        verify(visitType).setVolunteers(argThat(list -> list.contains(volunteer)));
    }
}