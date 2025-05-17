package it.unibs.ingsw.destinazioni.services;

import org.springframework.stereotype.Service;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import it.unibs.ingsw.destinazioni.domain.port.AreaOfInterestRepositoryPort;

@Service
public class AreaOfInterestService {
    
    AreaOfInterestRepositoryPort areaOfInterestRepositoryPort;

    public AreaOfInterestService(AreaOfInterestRepositoryPort areaOfInterestRepositoryPort) {
        this.areaOfInterestRepositoryPort = areaOfInterestRepositoryPort;
    }

    public void insertTown(String townName)
    {
        AreaOfInterest areaOfInterest = areaOfInterestRepositoryPort.load().orElse(new AreaOfInterest());
        areaOfInterest.getTowns().add(townName);
        areaOfInterestRepositoryPort.save(areaOfInterest);
    }

    public void removeTown(String townName)
    {
        AreaOfInterest areaOfInterest = areaOfInterestRepositoryPort.load().orElse(new AreaOfInterest());
        areaOfInterest.getTowns().remove(townName);
        areaOfInterestRepositoryPort.save(areaOfInterest);
    }

    public boolean isTownInAreaOfInterest(String townName)
    {
        AreaOfInterest areaOfInterest = areaOfInterestRepositoryPort.load().orElse(new AreaOfInterest());
        return areaOfInterest.getTowns().contains(townName);
    }

    public boolean isAreaOfInterestEmpty()
    {
        AreaOfInterest areaOfInterest = areaOfInterestRepositoryPort.load().orElse(new AreaOfInterest());
        return areaOfInterest.getTowns().isEmpty();
    }
    
}
