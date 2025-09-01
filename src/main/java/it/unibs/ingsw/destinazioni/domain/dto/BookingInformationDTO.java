package it.unibs.ingsw.destinazioni.domain.dto;

public record BookingInformationDTO(
		VisitInformationDTO visitInfo,
		String bookingCode
) {}