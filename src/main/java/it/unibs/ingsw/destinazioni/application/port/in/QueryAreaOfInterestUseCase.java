package it.unibs.ingsw.destinazioni.application.port.in;


public interface QueryAreaOfInterestUseCase {
    boolean containsTown(String townName);
    boolean isEmpty();
}
