package it.unibs.ingsw.destinazioni.application.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.unibs.ingsw.destinazioni.application.exceptions.usecases.LocationException;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;

class LocationServiceTest {
    private LocationRepositoryPort repository;
    private VisitTypeCommandUseCase visitTypeCRUD;
    private VisitTypeValidationUseCase visitTypeValidation;

    private LocationService service;

    @BeforeEach
    void setUp() {
        repository = mock(LocationRepositoryPort.class);
        visitTypeCRUD = mock(VisitTypeCommandUseCase.class);
        visitTypeValidation = mock(VisitTypeValidationUseCase.class);

        service = new LocationService(repository, visitTypeCRUD, visitTypeValidation);
    }

    @Test
    void removeLocation_LocationNotFound_ShouldThrowException() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(LocationException.class, () -> service.removeLocation(1));
    }

    @Test
    void removeLocation_LocationCannotBeRemoved_ShouldThrowException() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1);
        when(repository.findById(anyInt())).thenReturn(Optional.of(location));

        VisitType visitType = mock(VisitType.class);
        when(location.getVisitTypes()).thenReturn(List.of(visitType));

        assertThrows(LocationException.class, () -> service.removeLocation(1));
    }

    @Test
    void removeLocation_LocationHasNoVisitTypes_ShouldBeRemovedAsExpected() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1);
        when(repository.findById(anyInt())).thenReturn(Optional.of(location));

        when(location.getVisitTypes()).thenReturn(List.of());

        service.removeLocation(1);

        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void listAll_ShouldReturnAllLocationsInRepository() {
        Location locationTest = mock(Location.class);
        when(repository.findAll()).thenReturn(List.of(locationTest));

        List<Location> locations = service.listAll();
        assertNotNull(locations);
        assertEquals(1, locations.size());
        assertEquals(locationTest, locations.getFirst());
    }

    @Test
    void updateLocation_LocationIsNull_ShouldThrowException() {
        Location locationTest = mock(Location.class);

        assertThrows(LocationException.class, () -> service.updateLocation(locationTest));
    }

    @Test
    void findById_ShouldReturnLocation() {
        Location locationTest = mock(Location.class);
        when(repository.findById(1)).thenReturn(Optional.of(locationTest));

        service.findById(1);
        verify(repository, times(1)).findById(1);
    }

    @Test
    void updateLocation_LocationIsNotInRepository_ShouldThrowException() {
        Location locationTest = mock(Location.class);
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(LocationException.class, () -> service.updateLocation(locationTest));
    }

    @Test
    void updateLocation_LocationIsInRepository_ShouldSaveAsExpected() {
        Location locationTest = mock(Location.class);
        when(repository.findById(anyInt())).thenReturn(Optional.of(locationTest));

        service.updateLocation(locationTest);

        verify(repository, times(1)).save(locationTest);
    }

    @Test
    void canBeRemoved_VisitTypesEmpty_ShouldReturnTrue() {
        Location location = mock(Location.class);
        when(repository.findById(anyInt())).thenReturn(Optional.of(location));

        when(location.getVisitTypes()).thenReturn(List.of());

        Assertions.assertTrue(service.canBeRemoved(1));
    }

    @Test
    void canBeRemoved_LocationHasVisitTypes_ShouldReturnFalse() {
        Location location = mock(Location.class);
        when(repository.findById(anyInt())).thenReturn(Optional.of(location));

        VisitType visitType = mock(VisitType.class);
        when(location.getVisitTypes()).thenReturn(List.of(visitType));

        when(visitTypeValidation.canBeRemoved(anyInt())).thenReturn(false);
        Assertions.assertFalse(service.canBeRemoved(1));
    }

    @Test
    void getLocationForVisitType_LocationNotFound_ShouldThrowException() {
        when(repository.findByVisitType(any())).thenReturn(Optional.empty());
        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);

        assertThrows(LocationException.class, () -> service.getLocationForVisitType(visitType));
    }

    @Test
    void getLocationForVisitType_ShouldReturnLocation() {
        Location locationTest = mock(Location.class);
        VisitType visitType = mock(VisitType.class);
        when(visitType.getId()).thenReturn(1);

        when(repository.findByVisitType(visitType)).thenReturn(Optional.of(locationTest));

        service.getLocationForVisitType(visitType);
        verify(repository, times(1)).findByVisitType(visitType);
    }
}