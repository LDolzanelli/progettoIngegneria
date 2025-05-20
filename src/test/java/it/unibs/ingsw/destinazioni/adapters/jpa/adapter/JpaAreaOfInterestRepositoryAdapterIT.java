package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import it.unibs.ingsw.destinazioni.domain.model.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.LocationAddress;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaAreaOfInterestRepositoryAdapterIT  {

    @Autowired
    private JpaLocationRepositoryAdapter locationAdapter;

    @Autowired
    private AreaOfInterestRepositoryPort areaOfInterestRepositoryPort;

    @Test
    void saveLocationWithValidTown_shouldWork() {
        // Arrange - Add town to area of interest first
        areaOfInterestRepositoryPort.save(new AreaOfInterest(Set.of("Brescia")));
        
        LocationAddress address = new LocationAddress("Via Roma", "10", "Brescia", "BS");

        VisitType visitType = new VisitType(
            "Visita guidata", 
            "Descrizione visita", 
            "Ingresso principale", 
            LocalDate.now(), 
            LocalDate.now().plusMonths(1),
            LocalTime.of(10, 0), 
            90, 
            15, 
            5, 
            true, 
            List.of(DaysOfWeek.MONDAY, DaysOfWeek.WEDNESDAY),
            List.of()
        );
        
        Location location = new Location("Museo", "Museo della città", address, List.of(visitType));

        // Act
        locationAdapter.save(location);
        Location saved = locationAdapter.findAll().get(0);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Brescia", saved.getAddress().getTown());
    }


    @Test
    void findAll_shouldOnlyReturnLocationsWithValidTowns() {
        // Arrange - Setup area of interest
        areaOfInterestRepositoryPort.save(new AreaOfInterest(Set.of("Brescia", "Milano")));
        
        // Valid locations
        Location valid1 = new Location("Museo Brescia", "Desc", 
            new LocationAddress("Via 1", "1", "Brescia", "BS"), List.of());
        Location valid2 = new Location("Museo Milano", "Desc", 
            new LocationAddress("Via 2", "2", "Milano", "MI"), List.of());
            
        locationAdapter.save(valid1);
        locationAdapter.save(valid2);
        
        // Act
        List<Location> allLocations = locationAdapter.findAll();
        
        // Assert
        assertEquals(2, allLocations.size());
        assertTrue(allLocations.stream().anyMatch(l -> l.getAddress().getTown().equals("Brescia")));
        assertTrue(allLocations.stream().anyMatch(l -> l.getAddress().getTown().equals("Milano")));
    }
}