package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;
import it.unibs.ingsw.destinazioni.domain.model.Visit;


public interface VisitDaysUseCase {

    void createDefaultVisitDays(int month); //visits with default status ("PROPOSED")
    void updateVisitDays(int month);

    List<Visit> getConfirmedVisitsPerVolunteer(String volunteerNickname);

    Visit getVisitById(int visitId);

    
}
