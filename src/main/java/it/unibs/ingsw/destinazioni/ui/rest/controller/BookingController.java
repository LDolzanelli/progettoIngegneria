package it.unibs.ingsw.destinazioni.ui.rest.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import java.time.format.DateTimeFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.unibs.ingsw.destinazioni.application.port.in.BookingVisitsUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.ManageVisitTypeUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.BookingDetailsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.CancelBookingDTO;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.application.port.in.ManageLocationUseCase;
import it.unibs.ingsw.destinazioni.application.service.LocationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingVisitsUseCase bookingService;
    private final ManageLocationUseCase locationService;



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

        var dto = new BookingDetailsDTO(booking.getUser().getId(), booking.getUser().getNickname(),
                booking.getVisitorsNames(), visit.getVisitType().getTitle(), locationName, visit.getDate().format(dateFormatter));



        return ResponseEntity.ok(dto);

    }



}
