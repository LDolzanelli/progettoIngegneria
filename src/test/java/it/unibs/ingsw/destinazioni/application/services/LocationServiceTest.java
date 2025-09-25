package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LocationServiceTest {
    private LocationRepositoryPort repository;
    private VisitTypeCommandUseCase visitTypeCRUD;
    private VisitTypeValidationUseCase visitTypeValidation;

    LocationService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(LocationRepositoryPort.class);
        visitTypeCRUD = Mockito.mock(VisitTypeCommandUseCase.class);
        visitTypeValidation = Mockito.mock(VisitTypeValidationUseCase.class);

        service = new LocationService(repository, visitTypeCRUD, visitTypeValidation);
    }

    @Test
    void listAll_ShouldReturnAllLocationsInRepository() {
        Location locationTest = Mockito.mock(Location.class);
        Mockito.when(repository.findAll()).thenReturn(List.of(locationTest));

        List<Location> locations = service.listAll();
        assertNotNull(locations);
        assertEquals(1, locations.size());
        assertEquals(locationTest, locations.iterator().next());
    }

    @Test
    void canBeRemoved_ShouldReturnTrueIfVisitTypesIsEmpty() {
        Location location = Mockito.mock(Location.class);
        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(location));

        Mockito.when(location.getVisitTypes()).thenReturn(List.of());

        Assertions.assertTrue(service.canBeRemoved(1));
    }

    @Test
    void canBeRemoved_ShouldReturnFalseIfLocationHasVisitTypes() {
        Location location = Mockito.mock(Location.class);
        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(location));

        VisitType visitType = Mockito.mock(VisitType.class);
        List<VisitType> visitTypes = List.of(visitType);
        Mockito.when(location.getVisitTypes()).thenReturn(visitTypes);

        Mockito.when(visitTypeValidation.canBeRemoved(Mockito.anyInt())).thenReturn(false);
        Assertions.assertFalse(service.canBeRemoved(1));
    }
}