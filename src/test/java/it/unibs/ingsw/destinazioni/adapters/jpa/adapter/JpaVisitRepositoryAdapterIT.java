package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.AreaOfInterestEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationAddressEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationAddressIdEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.AreaOfInterestRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.LocationRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.UserRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitTypeRepository;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.LocationAddress;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitStatus;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;



@SpringBootTest
@Transactional
@ActiveProfiles("test")
class JpaVisitRepositoryAdapterIT {

    @Autowired
    private JpaVisitRepositoryAdapter adapter;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VisitTypeRepository visitTypeRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AreaOfInterestRepository areaOfInterestRepository;


    private UserEntity volunteer;
    private VisitTypeEntity visitType;

    @BeforeEach
    void setUp() {
        AreaOfInterestEntity town = new AreaOfInterestEntity();
        town.setTown("Brescia");
        areaOfInterestRepository.save(town);

        LocationAddressIdEntity addressId = new LocationAddressIdEntity();
        addressId.setStreet("Via Roma");
        addressId.setNumber("12");
        addressId.setTown("Brescia");

        LocationAddressEntity address = new LocationAddressEntity();
        address.setId(addressId);
        address.setProvince("BS");

        LocationEntity location = new LocationEntity();
        location.setName("Castello");
        location.setDescription("Antico castello");
        locationRepository.save(location);

        address.setLocation(location);
        location.setLocationAddress(address);

        locationRepository.save(location); // save location with address

        volunteer = new UserEntity();
        volunteer.setNickname("mario");
        volunteer.setPassword("pass");
        volunteer.setRole("VOLUNTEER");
        volunteer.setFirstLogin(false);
        userRepository.save(volunteer);


        visitType = new VisitTypeEntity();
        visitType.setTitle("Tour");
        visitType.setDescription("Descrizione");
        visitType.setMeetingPoint("Piazza");
        visitType.setStartDate(LocalDate.now());
        visitType.setEndDate(LocalDate.now().plusDays(1));
        visitType.setStartTime(LocalTime.of(10, 0));
        visitType.setDuration(90);
        visitType.setMaxNumParticipants(10);
        visitType.setMinNumParticipants(1);
        visitType.setIsFree(true);
        visitType.setLocation(location); 

        visitTypeRepository.save(visitType);
    }


    @Test
    void save_and_findById_shouldWork() {
        Visit visit =
                new Visit(null, LocalDate.now(),
                        new User(volunteer.getId(), volunteer.getNickname(), volunteer.getPassword(),
                                volunteer.getRole(), false),
                        "PROPOSED", toDomain(visitType), Set.of(), VisitStatus.PROPOSED);

        adapter.save(visit);
        Visit loaded = adapter.findAll().stream().findFirst().orElseThrow();

        assertEquals("mario", loaded.getVolunteer().getNickname());
        assertEquals("Tour", loaded.getVisitType().getTitle());
        assertEquals(VisitStatus.PROPOSED, loaded.getVisitStatus());
    }


    @Test
    void findAll_shouldReturnSavedVisit() {
        saveDummyVisit();
        Set<Visit> all = adapter.findAll();
        assertFalse(all.isEmpty());
    }


    @Test
    void findByVolunteer_shouldReturnCorrectVisits() {
        saveDummyVisit();
        Set<Visit> result = adapter.findByVolunteer("mario");
        assertEquals(1, result.size());
    }


    @Test
    void findByVisitType_shouldReturnCorrectVisits() {
        saveDummyVisit();
        Set<Visit> result = adapter.findByVisitType(visitType.getId());
        assertEquals(1, result.size());
    }


    @Test
    void deleteById_shouldRemoveVisit() {
        Visit visit = saveDummyVisit();
        adapter.deleteById(visit.getId());
        assertTrue(adapter.findById(visit.getId()).isEmpty());
    }


    private Visit saveDummyVisit() {
        Visit visit =
                new Visit(null, LocalDate.now(),
                        new User(volunteer.getId(), volunteer.getNickname(), volunteer.getPassword(),
                                volunteer.getRole(), false),
                        "PROPOSED", toDomain(visitType), Set.of(), VisitStatus.PROPOSED);
        adapter.save(visit);
        return adapter.findAll().stream().findFirst().orElseThrow();
    }


    private VisitType toDomain(VisitTypeEntity e) {
        return new VisitType(e.getId(), e.getTitle(), e.getDescription(), e.getMeetingPoint(), e.getStartDate(),
                e.getEndDate(), e.getStartTime(), e.getDuration(), e.getMaxNumParticipants(), e.getMinNumParticipants(),
                e.getIsFree(), List.of(), List.of());
    }
}
