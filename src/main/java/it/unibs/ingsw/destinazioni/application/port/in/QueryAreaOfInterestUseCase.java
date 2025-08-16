package it.unibs.ingsw.destinazioni.application.port.in;


import java.util.List;
import java.util.Map;

public interface QueryAreaOfInterestUseCase {
    boolean isEmpty();
    List<String> townList();
    Map<String, String> townProvinceMap();
}
