package it.unibs.ingsw.destinazioni.application.port.out;

public interface VisitPlanStatePort {

    boolean isVisitPlanCreated(int month, int year);
    void setVisitPlanCreated(int month, int year, boolean created);
    
}
