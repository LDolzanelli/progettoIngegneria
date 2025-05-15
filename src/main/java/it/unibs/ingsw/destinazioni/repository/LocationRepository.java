package it.unibs.ingsw.destinazioni.repository;

import it.unibs.ingsw.destinazioni.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
}