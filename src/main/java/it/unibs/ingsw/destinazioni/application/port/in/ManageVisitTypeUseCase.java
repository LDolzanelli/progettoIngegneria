package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import java.util.Optional;
import java.util.Set;

public interface ManageVisitTypeUseCase {
    void addVisitType(VisitType visitType, int locationId);
    void removeVisitType(int visitTypeId);
    Set<VisitType> listAll();
    Optional<VisitType> findById(int id);
    void updateVisitType(VisitType visitType, int locationId);
    Set<VisitType> listByLocation(int locationId);
    Set<VisitType> listByVolunteerId(int volunteerId);
    boolean canBeRemoved(int visitTypeId);

}
