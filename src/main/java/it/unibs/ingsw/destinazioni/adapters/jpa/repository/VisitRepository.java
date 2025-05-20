package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VisitRepository extends JpaRepository<VisitEntity, Integer> {
    public VisitEntity findByVisitType_Id(Integer visitTypeId);
    public List<VisitEntity> findByVolunteer_Id(Integer id);

}
