package it.unibs.ingsw.destinazioni.domain.dto;

import java.util.List;

public record LocationDTO(
    Integer id,
    String name,
    String description,
    LocationAddressDTO address,
    List<VisitTypeDTO> visitTypes
) {}
