package it.unibs.ingsw.destinazioni.application.port.in;


public interface ManageAreaOfInterestUseCase {
    void addTown(String townName);
    void removeTown(String townName);
}
