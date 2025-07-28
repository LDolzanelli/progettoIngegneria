package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;



import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.UserRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitTypeRepository;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;


/**
 * adapter per il repository delle visite
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
public class JpaVisitRepositoryAdapter implements VisitRepositoryPort {

    private final VisitRepository visitRepository;
    private final VisitTypeRepository visitTypeRepository;
    private final UserRepository userRepository;


    public JpaVisitRepositoryAdapter(VisitRepository visitRepository, VisitTypeRepository visitTypeRepository,
            UserRepository userRepository) {
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
        this.visitTypeRepository = visitTypeRepository;
    }


    @Override
    public void save(Visit visit) {
        VisitEntity entity = toEntity(visit);
        visitRepository.save(entity);
    }


    @Override
    public Optional<Visit> findById(int id) {
        return visitRepository.findById(id).map(this::toDomain);
    }


    @Override
    public void deleteById(int id) {
        visitRepository.deleteById(id);
    }


    @Override
    public Set<Visit> findAll() {
        return visitRepository.findAll().stream().map(this::toDomain).collect(Collectors.toSet());
    }


    @Override
    public Set<Visit> findByVolunteer(String nickname) {

        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return visitRepository.findByVolunteer_Id(user.getId()).stream().map(this::toDomain)
                .collect(Collectors.toSet());

    }


    @Override
    public Set<Visit> findByVisitType(int visitTypeId) {
        return Set.of(visitRepository.findByVisitType_Id(visitTypeId)).stream().map(this::toDomain)
                .collect(Collectors.toSet());
    }



    private Visit toDomain(VisitEntity entity) {
        //Volunteer potrebbe essere nullo quando la visita non é ancora stata assegnata a un volontario
        User volunteer;
        if(entity.getVolunteer() != null)
            volunteer = JpaUserRepositoryAdapter.toDomain(entity.getVolunteer());
        else volunteer = null;

        VisitType visitType = JpaVisitTypeRepositoryAdapter.toDomain(entity.getVisitType());
        Set<User> participants =
                entity.getVisitors().stream().map(JpaUserRepositoryAdapter::toDomain).collect(Collectors.toSet());
        VisitStatus status = VisitStatus.fromEnglishString(entity.getStatus());

        return new Visit(entity.getId(), entity.getDate(), volunteer, entity.getStatus(), visitType, participants,
                status);
    }


    private VisitEntity toEntity(Visit visit) {
        VisitEntity entity = new VisitEntity();

        if (visit.getId() != null)
            entity.setId(visit.getId());

        entity.setDate(visit.getDate());


        if (visit.getVolunteer() == null) {
            entity.setVolunteer(null);
        } else {
            UserEntity volunteer = userRepository.findById(visit.getVolunteer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Volunteer not found"));
            entity.setVolunteer(volunteer);
        }

        VisitTypeEntity visitType = visitTypeRepository.findById(visit.getVisitType().getId())
                .orElseThrow(() -> new IllegalArgumentException("VisitType not found"));
        entity.setVisitType(visitType);

        entity.setStatus(visit.getVisitStatus().name());

        Set<UserEntity> participants = visit.getParticipants().stream()
                .map(p -> userRepository.findById(p.getId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + p.getId())))
                .collect(Collectors.toSet());
        entity.setVisitors(participants);

        return entity;
    }

}
