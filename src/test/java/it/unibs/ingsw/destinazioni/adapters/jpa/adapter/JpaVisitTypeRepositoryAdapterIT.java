package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import it.unibs.ingsw.destinazioni.domain.model.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.LocationAddress;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.port.AreaOfInterestRepositoryPort;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaVisitTypeRepositoryAdapterIT {

    @Autowired
    private JpaVisitTypeRepositoryAdapter visitTypeAdapter;

    @Autowired
    private JpaLocationRepositoryAdapter locationAdapter;

    @Autowired
    private AreaOfInterestRepositoryPort areaOfInterestRepositoryPort;

    @Test
    void saveAndFindVisitType_shouldWork() {
        // Arrange - Setup location with valid town
        areaOfInterestRepositoryPort.save(new AreaOfInterest(Set.of("Brescia")));
        LocationAddress address = new LocationAddress("Via Roma", "10", "Brescia", "BS");
        Location location = new Location("Museo", "Desc", address, List.of());
        locationAdapter.save(location);
        int locationId = locationAdapter.findAll().get(0).getId();
        
        VisitType visitType = new VisitType(
            "Tour", 
            "Desc", 
            "Meeting point", 
            LocalDate.now(), 
            LocalDate.now().plusDays(1),
            LocalTime.of(10, 0), 
            60, 
            20, 
            5, 
            true, 
            List.of(DaysOfWeek.MONDAY, DaysOfWeek.FRIDAY)
        );

        // Act
        visitTypeAdapter.save(visitType, locationId);
        Set<VisitType> visitTypes = visitTypeAdapter.findByLocationId(locationId);

        // Assert
        assertEquals(1, visitTypes.size());
        VisitType saved = visitTypes.iterator().next();
        assertEquals("Tour", saved.getTitle());
        assertEquals(2, saved.getDaysAvailable().size());
    }

    @Test
    void findById_shouldReturnVisitType() {
        // Arrange - Setup test data
        areaOfInterestRepositoryPort.save(new AreaOfInterest(Set.of("Brescia")));
        LocationAddress address = new LocationAddress("Via Roma", "10", "Brescia", "BS");
        Location location = new Location("Museo", "Desc", address, List.of());
        locationAdapter.save(location);
        int locationId = locationAdapter.findAll().get(0).getId();
        
        VisitType visitType = new VisitType(
            "Tour", "Desc", "Point", 
            LocalDate.now(), LocalDate.now().plusDays(1),
            LocalTime.of(10, 0), 60, 20, 5, true, 
            List.of(DaysOfWeek.MONDAY)
        );
        visitTypeAdapter.save(visitType, locationId);
        int visitTypeId = visitTypeAdapter.findAll().iterator().next().getId();

        // Act
        Optional<VisitType> found = visitTypeAdapter.findById(visitTypeId);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Tour", found.get().getTitle());
    }

    @Test
    void deleteVisitType_shouldWork() {
        // Arrange
        areaOfInterestRepositoryPort.save(new AreaOfInterest(Set.of("Brescia")));
        LocationAddress address = new LocationAddress("Via Roma", "10", "Brescia", "BS");
        Location location = new Location("Museo", "Desc", address, List.of());
        locationAdapter.save(location);
        int locationId = locationAdapter.findAll().get(0).getId();
        
        VisitType visitType = new VisitType(
            "ToDelete", "Desc", "Point", 
            LocalDate.now(), LocalDate.now().plusDays(1),
            LocalTime.of(10, 0), 60, 20, 5, true, 
            List.of(DaysOfWeek.MONDAY)
        );
        visitTypeAdapter.save(visitType, locationId);
        int visitTypeId = visitTypeAdapter.findAll().iterator().next().getId();

        // Act
        visitTypeAdapter.deleteById(visitTypeId);

        // Assert
        assertTrue(visitTypeAdapter.findById(visitTypeId).isEmpty());
    }
}