package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.LocationEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitDayEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitDayIdEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.LocationRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitTypeRepository;
import it.unibs.ingsw.destinazioni.domain.model.DaysOfWeek;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.port.VisitTypeRepositoryPort;
import java.util.List;
import java.util.Optional;


/**
 * Adapter per il repository dei tipi di visita.
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
public class JpaVisitTypeRepositoryAdapter implements VisitTypeRepositoryPort {

    private final VisitTypeRepository visitTypeRepository;
    private final LocationRepository locationRepository;

    public JpaVisitTypeRepositoryAdapter(VisitTypeRepository visitTypeRepository,
            LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.visitTypeRepository = visitTypeRepository;
    }


    @Override
    public void save(VisitType visitType, int locationId) {
        LocationEntity location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        VisitTypeEntity entity = toEntity(visitType, location);
        visitTypeRepository.save(entity); 
    }


    @Override
    public Optional<VisitType> findById(int id) {
        return visitTypeRepository.findById(id).map(JpaVisitTypeRepositoryAdapter::toDomain);
    }


    @Override
    public Set<VisitType> findAll() {
        return visitTypeRepository.findAll().stream().map(JpaVisitTypeRepositoryAdapter::toDomain).collect(Collectors.toSet());
    }


    @Override
    public void deleteById(int id) {
        visitTypeRepository.deleteById(id);
    }


    @Override
    public Set<VisitType> findByLocationId(int locationId) {
        return visitTypeRepository.findByLocation_Id(locationId).stream().map(JpaVisitTypeRepositoryAdapter::toDomain)
                .collect(Collectors.toSet());
    }



    protected static VisitType toDomain(VisitTypeEntity entity) {
        List<DaysOfWeek> days = entity.getVisitDayEntities().stream().map(VisitDayEntity::getId)
                .map(id -> DaysOfWeek.fromEnglishString(id.getDayOfWeek())).toList();

        return new VisitType(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getMeetingPoint(),
                entity.getStartDate(), entity.getEndDate(), entity.getStartTime(), entity.getDuration(),
                entity.getMaxNumParticipants(), entity.getMinNumParticipants(), entity.getIsFree(), days);
    }



    protected static VisitTypeEntity toEntity(VisitType visitType, LocationEntity locationEntity) {
        VisitTypeEntity entity = new VisitTypeEntity();
        entity.setTitle(visitType.getTitle());
        entity.setDescription(visitType.getDescription());
        entity.setMeetingPoint(visitType.getMeetingPoint());
        entity.setStartDate(visitType.getStartDate());
        entity.setEndDate(visitType.getEndDate());
        entity.setStartTime(visitType.getStartTime());
        entity.setDuration(visitType.getDuration());
        entity.setIsFree(visitType.isFree());
        entity.setMaxNumParticipants(visitType.getMaxParticipants());
        entity.setMinNumParticipants(visitType.getMinParticipants());
        entity.setLocation(locationEntity);

        Set<VisitDayEntity> days = visitType.getDaysAvailable().stream().map(day -> {
            VisitDayEntity dayEntity = new VisitDayEntity();
            VisitDayIdEntity id = new VisitDayIdEntity();
            id.setDayOfWeek(day.toString());
            dayEntity.setId(id);
            dayEntity.setVisitTypeEntity(entity);
            return dayEntity;
        }).collect(Collectors.toSet());

        entity.setVisitDayEntities(days);

        return entity;
    }



}


