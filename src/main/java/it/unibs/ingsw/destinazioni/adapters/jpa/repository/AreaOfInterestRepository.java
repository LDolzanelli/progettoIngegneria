package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.AreaOfInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaOfInterestRepository extends JpaRepository<AreaOfInterestEntity, String> {
    
}
