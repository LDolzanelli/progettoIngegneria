package it.unibs.ingsw.destinazioni.application.port.in;

public interface ChangeMaxNumberOfTicketsUseCase {
    void setMaxNumberOfTickets(int maxNumberOfTickets);
    int getMaxNumberOfTickets();
}
