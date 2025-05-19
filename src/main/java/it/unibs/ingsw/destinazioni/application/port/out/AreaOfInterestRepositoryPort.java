package it.unibs.ingsw.destinazioni.application.port.out;

import java.util.Optional;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;

public interface AreaOfInterestRepositoryPort {

    void save(AreaOfInterest areaOfInterest);
    Optional<AreaOfInterest> load();
}