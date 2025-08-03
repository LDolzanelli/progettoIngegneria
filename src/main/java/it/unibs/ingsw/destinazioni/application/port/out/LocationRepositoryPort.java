package it.unibs.ingsw.destinazioni.application.port.out;

import java.util.List;
import java.util.Optional;
import it.unibs.ingsw.destinazioni.domain.model.Location;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;

public interface LocationRepositoryPort {

    void save(Location location);
    Optional<Location> findById(int id);

    List<Location> findAll();
    void deleteById(int id);

    Optional<Location> findByVisitType(VisitType visitType);

}
