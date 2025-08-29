package it.unibs.ingsw.destinazioni.ui.rest.controller;

import it.unibs.ingsw.destinazioni.application.port.in.*;
import it.unibs.ingsw.destinazioni.domain.dto.BookingRequestDTO;
import it.unibs.ingsw.destinazioni.domain.model.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.unibs.ingsw.destinazioni.domain.dto.BookingDetailsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.CancelBookingDTO;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.application.service.LocationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingVisitsUseCase bookingService;
    private final ManageLocationUseCase locationService;
    private final GetUserInfoUseCase userService;
    private final VisitDaysUseCase visitService;

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBooking(@RequestBody CancelBookingDTO dto) {
        String bookingCode = dto.bookingCode();
        Integer userId = dto.userId();

        try {
            bookingService.cancelBooking(bookingCode, userId);
            return ResponseEntity.ok("Prenotazione " + bookingCode + " è stata annullata con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Errore durante l'annullamento della prenotazione: " + e.getMessage());
        }
    }

    @GetMapping("/booking-details/{bookingCode}")
    public ResponseEntity<BookingDetailsDTO> bookingDetails(@PathVariable String bookingCode) {
        Booking booking = bookingService.getBookingByCode(bookingCode);
        Visit visit = bookingService.getVisitByBookingCode(bookingCode);
        String locationName = locationService.getLocationForVisitType(visit.getVisitType()).getName();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var dto = new BookingDetailsDTO(booking.getUser().getId(), visit.getId(), booking.getUser().getNickname(),
                booking.getVisitorsNames(), visit.getVisitType().getTitle(), locationName, visit.getDate().format(dateFormatter));

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookVisit(@RequestBody BookingRequestDTO dto) {
        Visit visit = visitService.getVisitById(dto.visitId());
        User user = userService.findById(dto.userId()) //
                .orElseThrow(() -> new IllegalArgumentException("User with id " + dto.userId() + " not found"));
        List<String> visitorNames = dto.visitorsNames();

        try {
            bookingService.bookVisit(visit, user, visitorNames);
            return ResponseEntity.ok("Prenotazione effettuata con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Errore durante la prenotazione: " + e.getMessage());
        }
    }
}
