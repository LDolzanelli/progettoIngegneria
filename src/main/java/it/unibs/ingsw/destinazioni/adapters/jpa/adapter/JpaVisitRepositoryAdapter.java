package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingId;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitTypeEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.BookingRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.UserRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitTypeRepository;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;


/**
 * adapter per il repository delle visite
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class JpaVisitRepositoryAdapter implements it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort {

    private final VisitRepository visitRepository;
    private final VisitTypeRepository visitTypeRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;



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
        User volunteer =
                entity.getVolunteer() != null ? JpaUserRepositoryAdapter.toDomain(entity.getVolunteer()) : null;

        VisitType visitType = JpaVisitTypeRepositoryAdapter.toDomain(entity.getVisitType());

        List<Booking> bookings = entity.getBookings().stream()
                .collect(Collectors.groupingBy(b -> b.getId().getBookingCode())).entrySet().stream().map(entry -> {
                    var bookingEntities = entry.getValue();
                    User user = JpaUserRepositoryAdapter.toDomain(bookingEntities.get(0).getUser());
                    List<String> visitors = bookingEntities.stream().map(b -> b.getId().getVisitorName()).toList();
                    return new Booking(entry.getKey(), user, visitors);
                }).toList();

        VisitStatus status = VisitStatus.fromEnglishString(entity.getStatus());

        return new Visit(entity.getId(), entity.getDate(), volunteer, visitType, bookings, status);
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

        List<BookingEntity> bookingEntities = new ArrayList<>();
        for (Booking booking : visit.getBookings()) {
            bookingEntities.addAll(bookingToEntity(booking, entity));
        }
        entity.setBookings(bookingEntities);

        return entity;
    }



    private List<BookingEntity> bookingToEntity(Booking booking, VisitEntity visitEntity) {
        UserEntity userEntity = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return booking.getVisitorsNames().stream().map(name -> {
            BookingEntity bookingEntity = new BookingEntity();
            BookingId bookingId = new BookingId(booking.getBookingCode(), name);
            bookingEntity.setId(bookingId);
            bookingEntity.setUser(userEntity);
            bookingEntity.setVisit(visitEntity);
            return bookingEntity;
        }).toList();
    }


}
