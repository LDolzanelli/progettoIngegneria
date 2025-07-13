package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationAddressEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationAddressIdEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitDayEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteersVisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.LocationRepository;
import it.unibs.ingsw.destinazioni.application.port.out.LocationRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.LocationAddress;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.DaysOfWeek;



/**
 * Adapter per il repository delle location.
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
public class JpaLocationRepositoryAdapter implements LocationRepositoryPort {

    private final LocationRepository locationRepository;

    public JpaLocationRepositoryAdapter(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    @Override
    public void save(Location location) {
        locationRepository.save(toEntity(location));
    }



    @Override
    public Optional<Location> findById(int id) {
        return locationRepository.findById(id).map(this::toDomain);
    }


    @Override
    public List<Location> findAll() {
        return locationRepository.findAll().stream().map(this::toDomain).toList();
    }


    @Override
    public void deleteById(int id) {

        locationRepository.deleteById(id);
    }


    public LocationEntity toEntity(Location location) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setId(location.getId());
        locationEntity.setName(location.getName());
        locationEntity.setDescription(location.getDescription());

        // Costruzione dell’indirizzo
        LocationAddressEntity addressEntity = new LocationAddressEntity();

        LocationAddressIdEntity addressId = new LocationAddressIdEntity();
        addressId.setStreet(location.getAddress().getStreet());
        addressId.setNumber(location.getAddress().getStreetNumber());
        addressId.setTown(location.getAddress().getTown());

        addressEntity.setId(addressId);
        addressEntity.setProvince(location.getAddress().getProvince());

        // Collegamento bidirezionale
        addressEntity.setLocation(locationEntity);
        locationEntity.setLocationAddress(addressEntity);

        Set<VisitTypeEntity> visitTypeEntities = location.getVisitTypes().stream().map(visitType -> {
            VisitTypeEntity visitTypeEntity = new VisitTypeEntity();
            visitTypeEntity.setTitle(visitType.getTitle());
            visitTypeEntity.setDescription(visitType.getDescription());
            visitTypeEntity.setMeetingPoint(visitType.getMeetingPoint());
            visitTypeEntity.setStartDate(visitType.getStartDate());
            visitTypeEntity.setEndDate(visitType.getEndDate());
            visitTypeEntity.setStartTime(visitType.getStartTime());
            visitTypeEntity.setDuration(visitType.getDuration());
            visitTypeEntity.setIsFree(visitType.isFree());
            visitTypeEntity.setMaxNumParticipants(visitType.getMaxParticipants());
            visitTypeEntity.setMinNumParticipants(visitType.getMinParticipants());

            // Collegamento bidirezionale
            visitTypeEntity.setLocation(locationEntity);
            return visitTypeEntity;
        }).collect(Collectors.toSet());

        locationEntity.setVisitTypeEntities(visitTypeEntities);

        return locationEntity;
    }



    public Location toDomain(LocationEntity entity) {

        LocationAddress address = new LocationAddress(entity.getLocationAddress().getId().getStreet(),
                entity.getLocationAddress().getId().getNumber(), entity.getLocationAddress().getId().getTown(),
                entity.getLocationAddress().getProvince());

        List<VisitType> visitTypes = entity.getVisitTypeEntities().stream().map(visitTypeEntity -> {

            List<DaysOfWeek> days = visitTypeEntity.getVisitDayEntities().stream().map(VisitDayEntity::getId)
                    .map(id -> DaysOfWeek.fromEnglishString(id.getDayOfWeek())).toList();

            List<User> volunteers = visitTypeEntity.getVolunteersVisitTypeEntities().stream()
                    .map(VolunteersVisitTypeEntity::getVolunteer).map(JpaUserRepositoryAdapter::toDomain).toList();

            return new VisitType(visitTypeEntity.getId(), visitTypeEntity.getTitle(), visitTypeEntity.getDescription(),
                    visitTypeEntity.getMeetingPoint(), visitTypeEntity.getStartDate(), visitTypeEntity.getEndDate(),
                    visitTypeEntity.getStartTime(), visitTypeEntity.getDuration(),
                    visitTypeEntity.getMaxNumParticipants(), visitTypeEntity.getMinNumParticipants(),
                    visitTypeEntity.getIsFree(), days, volunteers);
        }).toList();


        return new Location(entity.getId(), entity.getName(), entity.getDescription(), address, visitTypes);

    }



}
