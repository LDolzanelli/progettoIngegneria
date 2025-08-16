package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.User;

public interface ManageVolunteersUseCase {

    boolean canBeRemoved(User volunteer);
    void removeVolunteer(User volunteer);
}
