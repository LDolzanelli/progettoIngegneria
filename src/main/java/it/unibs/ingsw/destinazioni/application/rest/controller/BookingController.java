package it.unibs.ingsw.destinazioni.application.rest.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibs.ingsw.destinazioni.application.port.in.booking.BookingQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.booking.CancelBookingUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.booking.CreateBookingUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.location.LocationQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visit.VisitDaysUseCase;
import it.unibs.ingsw.destinazioni.domain.dto.BookingDetailsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.BookingInformationDTO;
import it.unibs.ingsw.destinazioni.domain.dto.BookingRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.CancelBookingDTO;
import it.unibs.ingsw.destinazioni.domain.dto.VisitInformationDTO;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.application.rest.mapper.VisitMapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private final CreateBookingUseCase createBookingService;
    private final CancelBookingUseCase cancelBookingService;
    private final BookingQueryUseCase bookingQueryService;
    private final LocationQueryUseCase locationQueryService;
    private final GetUserInfoUseCase userService;
    private final VisitDaysUseCase visitService;

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBooking(@RequestBody CancelBookingDTO dto) {
        String bookingCode = dto.bookingCode();
        Integer userId = dto.userId();

        try {
            cancelBookingService.cancelBooking(bookingCode, userId);
            return ResponseEntity.ok("Prenotazione " + bookingCode + " è stata annullata con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Errore durante l'annullamento della prenotazione: " + e.getMessage());
        }
    }


    @GetMapping("/booking-details/{bookingCode}")
    public ResponseEntity<BookingDetailsDTO> bookingDetails(@PathVariable String bookingCode) {
        Booking booking = bookingQueryService.getBookingByCode(bookingCode);
        Visit visit = bookingQueryService.getVisitByBookingCode(bookingCode);
        String locationName = locationQueryService.getLocationForVisitType(visit.getVisitType()).getName();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        var dto = new BookingDetailsDTO(booking.getUser().getId(), visit.getId(), booking.getUser().getNickname(),
                booking.getVisitorsNames(), visit.getVisitType().getTitle(), locationName,
                visit.getDate().format(dateFormatter));

        return ResponseEntity.ok(dto);
    }


    @PostMapping("/book")
    public ResponseEntity<String> bookVisit(@RequestBody BookingRequestDTO dto) {
        Visit visit = visitService.getVisitById(dto.visitId());
        User user = userService.findById(dto.userId()) //
                .orElseThrow(() -> new IllegalArgumentException("User with id " + dto.userId() + " not found"));
        List<String> visitorNames = dto.visitorsNames();

        try {
            createBookingService.bookVisit(visit, user, visitorNames);
            return ResponseEntity.ok("Prenotazione effettuata con successo");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Errore durante la prenotazione: " + e.getMessage());
        }
    }

    private final VisitMapper visitMapper;

    @GetMapping("/my-bookings/{userId}")
    public ResponseEntity<List<BookingInformationDTO>> getMyBookingsWithCode(@PathVariable int userId) {
        User user = userService.findById(userId) //
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Booking> bookings = bookingQueryService.getBookingsByUser(user);

        List<BookingInformationDTO> result = bookings.stream() //
                .map(b -> { //
                    Visit visit = bookingQueryService.getVisitByBookingCode(b.getBookingCode()); //
                    VisitInformationDTO dto = visitMapper.toVisitInformationDTO(visit); //
                    return new BookingInformationDTO(dto, b.getBookingCode()); //
                }) //
                .toList();

        return ResponseEntity.ok(result);
    }
}
