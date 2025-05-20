package it.unibs.ingsw.destinazioni.application.port.in;


import java.util.List;

public interface QueryAreaOfInterestUseCase {
    boolean containsTown(String townName);
    boolean isEmpty();
    List<String> townList();
}
