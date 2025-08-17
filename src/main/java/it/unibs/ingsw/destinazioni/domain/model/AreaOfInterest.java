package it.unibs.ingsw.destinazioni.domain.model;

import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public class AreaOfInterest {

    private final Set<TownProvinceDTO> areas;

    public AreaOfInterest(Set<TownProvinceDTO> areas) {
        this.areas = areas != null ? areas : new HashSet<>();
    }

    public AreaOfInterest() {
        this.areas = new HashSet<>();
    }

    public void addArea(TownProvinceDTO dto) {
        areas.add(dto); // Set garantisce unicità
    }
}