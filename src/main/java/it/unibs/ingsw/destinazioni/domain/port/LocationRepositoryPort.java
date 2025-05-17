package it.unibs.ingsw.destinazioni.domain.port;

import java.util.List;
import java.util.Optional;
import it.unibs.ingsw.destinazioni.domain.model.Location;

public interface LocationRepositoryPort {

    void save(Location location);
    Optional<Location> findById(int id);

    List<Location> findAll();
    void deleteById(int id);
}
