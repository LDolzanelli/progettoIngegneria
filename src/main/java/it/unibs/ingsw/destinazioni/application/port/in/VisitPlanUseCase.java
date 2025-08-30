package it.unibs.ingsw.destinazioni.application.port.in;

import java.util.List;
import it.unibs.ingsw.destinazioni.domain.model.Visit;

public interface VisitPlanUseCase {

    public boolean canCreateVisitPlan();
    public void createVisitPlan();
    public List<Visit> getVisitPlan(int month, int year);
    public int getMonth();
    public int getYear();
}
