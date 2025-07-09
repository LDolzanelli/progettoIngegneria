package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteerAvailableDateEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteerAvailableDateEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerAvailableDateRepository extends JpaRepository<VolunteerAvailableDateEntity, VolunteerAvailableDateEntityId> {
    
}