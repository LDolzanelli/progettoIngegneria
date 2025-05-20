package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteersVisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VolunteersVisitTypeEntityId;

@Repository
public interface VolunteerVisitTypeRepository extends JpaRepository<VolunteersVisitTypeEntity, VolunteersVisitTypeEntityId>{
    
}
