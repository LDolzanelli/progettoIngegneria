package it.unibs.ingsw.destinazioni.domain.dto;

import org.thymeleaf.expression.Dates;

import java.time.LocalDate;
import java.util.List;

public record BlockedDatesDTO(List<String> dateList){

}
