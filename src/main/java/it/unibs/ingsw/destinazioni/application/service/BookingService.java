package it.unibs.ingsw.destinazioni.application.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.BookingVisitsUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import lombok.RequiredArgsConstructor;
import it.unibs.ingsw.destinazioni.application.port.out.BookingRepositoryPort;
import it.unibs.ingsw.destinazioni.application.util.BookingCodeGenerator;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

@Service
@RequiredArgsConstructor
public class BookingService implements BookingVisitsUseCase {

    private final BookingRepositoryPort bookingRepository;
    private final GetUserInfoUseCase userInfoService;
    private final VisitRepositoryPort visitRepository;

    @Override
    public void bookVisit(Visit visit, User user, List<String> visitorsNames) {

        String bookingCode;

        do {
            bookingCode = BookingCodeGenerator.generateBookingCode();
        } while (bookingRepository.findByBookingCode(bookingCode).isPresent());

        userInfoService.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + user.getId() + " not found"));

        visitRepository.findById(visit.getId())
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visit.getId() + " not found"));

        if (user.getRole() != Role.FINAL_USER) {
            throw new IllegalArgumentException("Only users with role FINAL_USER can book a visit");
        }

        int visitorsCount = visitorsNames.size();

        if (visitorsCount > visit.getAvailableSeats()) {
            throw new IllegalArgumentException("Not enough available seats for this visit (" + visit.getAvailableSeats()
                    + " available, " + visitorsCount + " requested)");
        }


        Booking booking = new Booking(bookingCode, user, visitorsNames);
        bookingRepository.save(booking, visit.getId());

    }


    @Override
    public List<Booking> getBookingsByVisit(Visit visit) {

        visitRepository.findById(visit.getId())
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visit.getId() + " not found"));

        return bookingRepository.findAllByVisitId(visit.getId());
    }


    @Override
    public List<Booking> getBookingsByUser(User user) {


        userInfoService.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + user.getId() + " not found"));

        return bookingRepository.findAllByUserId(user.getId());
    }


    @Override
    public void cancelBooking(String bookingCode) {

        bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new IllegalArgumentException("No booking found with booking code: " + bookingCode));

        if (!isThisBookingCancellable(bookingCode)) {
            throw new IllegalStateException("This booking cannot be cancelled (less than 3 days to the visit)");
        }

        bookingRepository.deleteByBookingCode(bookingCode);
    }


    @Override
    public boolean isThisBookingCancellable(String bookingCode) {
        // TODO: implementare la logica per verificare se la prenotazione è cancellabile (entro 3 giorni
        // dalla visita)

        return false;
    }



}
