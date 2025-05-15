package it.unibs.ingsw.destinazioni.repository;

import it.unibs.ingsw.destinazioni.entity.VisitTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitTypeRepository extends JpaRepository<VisitTypeEntity, Integer> {
}