package it.unibs.ingsw.destinazioni.domain.model;

import java.util.Set;
import lombok.Getter;

@Getter
public class AreaOfInterest {

    private final Set<String> towns;

    public AreaOfInterest(Set<String> towns) {
        this.towns = towns;
    }

    public AreaOfInterest() {
        this.towns = Set.of();
    }
    
}
