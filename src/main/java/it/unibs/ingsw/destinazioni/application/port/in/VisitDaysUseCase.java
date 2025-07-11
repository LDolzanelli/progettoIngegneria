package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.VisitType;

public interface VisitDaysUseCase {

    void createDefaultVisitDays(int month);
    void updateVisitDays(int month);

    
}
