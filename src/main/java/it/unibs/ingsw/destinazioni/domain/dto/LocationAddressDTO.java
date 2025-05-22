package it.unibs.ingsw.destinazioni.domain.dto;

public record LocationAddressDTO(
    String street,
    String streetNumber,
    String town,
    String province
) {}
