package it.unibs.ingsw.destinazioni.application.port.out;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import java.util.Set;
import java.util.Optional;

public interface VisitTypeRepositoryPort {
    void save(VisitType visitType, int locationId);
    Optional<VisitType> findById(int id);
    void deleteById(int id);
    Set<VisitType> findAll();
    Set<VisitType> findByLocationId(int locationId);
    
}
