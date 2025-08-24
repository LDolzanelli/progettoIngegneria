package it.unibs.ingsw.destinazioni.application.port.out;

import java.util.Optional;
import java.util.Set;

import it.unibs.ingsw.destinazioni.domain.model.Visit;

public interface VisitRepositoryPort {
    void save(Visit visit);
    Optional<Visit> findById(int id);
    void deleteById(int id);
    Set<Visit> findAll();
    Set<Visit> findByVolunteer(String volunteerNickname);
    Set<Visit> findByVisitType(int visitTypeId);
    Optional<Visit> findByBookingCode(String bookingCode);
}