package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingId;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.VisitEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.BookingRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.UserRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.VisitRepository;
import it.unibs.ingsw.destinazioni.application.port.out.BookingRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class JpaBookingAdapter implements BookingRepositoryPort {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;


    @Override
    public void save(Booking booking, int visitId) {
        List<BookingEntity> entities = toEntity(booking, visitId);
        bookingRepository.saveAll(entities);
    }


    @Override
    public void deleteByBookingCode(String bookingCode) {

        List<BookingEntity> bookings = bookingRepository.findByIdBookingCode(bookingCode);

        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No bookings found with booking code: " + bookingCode);
        }

        bookingRepository.deleteAll(bookings);
    }


    @Override
    public Optional<Booking> findByBookingCode(String bookingCode) {
        List<BookingEntity> bookings = bookingRepository.findByIdBookingCode(bookingCode);

        if (bookings.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toDomain(bookingCode, bookings));
    }


    @Override
    public List<Booking> findAll() {
        List<BookingEntity> entities = bookingRepository.findAll();

        return entities.stream().collect(Collectors.groupingBy(e -> e.getId().getBookingCode())).entrySet().stream()
                .map(entry -> toDomain(entry.getKey(), entry.getValue())).toList();
    }


    @Override
    public List<Booking> findAllByVisitId(int visitId) {
        List<BookingEntity> entities = bookingRepository.findByVisit_Id(visitId);

        return entities.stream().collect(Collectors.groupingBy(e -> e.getId().getBookingCode())).entrySet().stream()
                .map(entry -> toDomain(entry.getKey(), entry.getValue())).toList();
    }


    @Override
    public List<Booking> findAllByUserId(int userId) {
        List<BookingEntity> bookings = bookingRepository.findByUser_Id(userId);

        return bookings.stream().collect(Collectors.groupingBy(e -> e.getId().getBookingCode())).entrySet().stream()
                .map(entry -> toDomain(entry.getKey(), entry.getValue())).toList();
    }


    private Booking toDomain(String bookingCode, List<BookingEntity> entities) {
        if (entities.isEmpty()) {
            throw new IllegalArgumentException("No bookings found with booking code: " + bookingCode);
        }

        User user = JpaUserRepositoryAdapter.toDomain(entities.getFirst().getUser());
        List<String> visitorsNames = entities.stream().map(e -> e.getId().getVisitorName()).toList();

        return new Booking(bookingCode, user, visitorsNames);
    }


    private List<BookingEntity> toEntity(Booking booking, int visitId) {

        return booking.getVisitorsNames().stream().map(name -> {
            BookingEntity bookingEntity = new BookingEntity();
            BookingId bookingId = new BookingId();
            bookingId.setBookingCode(booking.getBookingCode());
            bookingId.setVisitorName(name);
            bookingEntity.setId(bookingId);
            UserEntity userEntity = userRepository.findById(booking.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            bookingEntity.setUser(userEntity);
            VisitEntity visitEntity = visitRepository.findById(visitId)
                    .orElseThrow(() -> new IllegalArgumentException("Visit not found"));
            bookingEntity.setVisit(visitEntity);
            return bookingEntity;
        }).toList();

    }

}
