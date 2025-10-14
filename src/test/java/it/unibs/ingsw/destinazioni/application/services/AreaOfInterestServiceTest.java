package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AreaOfInterestServiceTest {

    private AreaOfInterestRepositoryPort repository;

    private AreaOfInterestService service;

    @BeforeEach
    void setUp() {
        repository = mock(AreaOfInterestRepositoryPort.class);

        service = new AreaOfInterestService(repository);
    }

    @Test
    void isEmpty_AreaIsEmpty_ShouldReturnTrue() {
        AreaOfInterest areaOfInterest = new AreaOfInterest();
        when(repository.load()).thenReturn(Optional.of(areaOfInterest));

        assertTrue(service.isEmpty());
    }

    @Test
    void isEmpty_AreaHasTowns_ShouldReturnFalse() {
        AreaOfInterest areaOfInterest = new AreaOfInterest();
        when(repository.load()).thenReturn(Optional.of(areaOfInterest));
        areaOfInterest.addArea(new TownProvinceDTO("town", "province"));

        assertFalse(service.isEmpty());
    }


    @Test
    void addArea_ShouldSaveAsExpected() {
        AreaOfInterest areaOfInterest = new AreaOfInterest();
        when(repository.load()).thenReturn(Optional.of(areaOfInterest));

        service.addArea("town", "province");
        verify(repository).save(areaOfInterest);
    }
}