package it.unibs.ingsw.destinazioni.application.port.in;


public interface VisitDaysUseCase {

    void createDefaultVisitDays(int month); //visits with default status ("PROPOSED")
    void updateVisitDays(int month);

    
}
