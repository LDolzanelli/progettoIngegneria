package it.unibs.ingsw.destinazioni.application.port.out;

import it.unibs.ingsw.destinazioni.domain.model.Visit;
import java.util.Optional;
import java.util.Set;

public interface VisitRepositoryPort {
    void save(Visit visit);
    Optional<Visit> findById(int id);
    void deleteById(int id);
    Set<Visit> findAll();
    Set<Visit> findByVolunteer(String volunteerNickname);
    Set<Visit> findByVisitType(int visitTypeId);
}