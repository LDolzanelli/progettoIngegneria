package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;

@Repository
public interface VisitTypeRepository extends JpaRepository<VisitTypeEntity, Integer> {
    public List<VisitTypeEntity> findByLocation_Id(Integer locationId);
}