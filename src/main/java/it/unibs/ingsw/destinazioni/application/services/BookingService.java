package it.unibs.ingsw.destinazioni.application.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.BookingErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.codes.VisitDayErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.BookingException;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.UserException;
import it.unibs.ingsw.destinazioni.application.exceptions.specific.VisitDayException;
import it.unibs.ingsw.destinazioni.application.port.in.booking.BookingQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.booking.CancelBookingUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.booking.CreateBookingUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.user.GetUserInfoUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.BookingRepositoryPort;
import it.unibs.ingsw.destinazioni.application.port.out.VisitRepositoryPort;
import it.unibs.ingsw.destinazioni.application.util.BookingCodeGenerator;
import it.unibs.ingsw.destinazioni.domain.model.Booking;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.Visit;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService implements CreateBookingUseCase, BookingQueryUseCase, CancelBookingUseCase {

  private final BookingRepositoryPort bookingRepository;
  private final GetUserInfoUseCase userInfoService;
  private final VisitRepositoryPort visitRepository;

  @Override
  /*@ also
    @ requires bookingRepository != null && userInfoService != null && visitRepository != null;
    @ ensures (\exists Booking b; getBookingsByVisit(visit).contains(b);
    @          b.getUser().getId().equals(user.getId()) && 
    @          b.getVisitorsNames().size() == visitorsNames.size());
    @ ensures visit.getAvailableSeats() == \old(visit.getAvailableSeats()) - visitorsNames.size();
    @*/
  public void bookVisit(Visit visit, User user, List<String> visitorsNames) {
    String bookingCode;

    do {
      bookingCode = BookingCodeGenerator.generateBookingCode();
    } while (bookingRepository.findByBookingCode(bookingCode).isPresent());

    userInfoService.findById(user.getId()).orElseThrow(
        () -> new UserException(UserErrorCode.USER_NOT_FOUND, "User with id " + user.getId() + " not found"));

    visitRepository.findById(visit.getId())
        .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visit.getId() + " not found"));

    if (user.getRole() != Role.FINAL_USER) {
      throw new UserException(UserErrorCode.UNAUTHORIZED_REQUEST,
          "Solo gli utenti di tipo FINAL_USER possono prenotare visite");
    }

    int visitorsCount = visitorsNames.size();

    if (visitorsCount > visit.getAvailableSeats()) {
      throw new BookingException(BookingErrorCode.NOT_ENOUGH_SEATS,
          "Not enough available seats for this visit, available: " + visit.getAvailableSeats() + ", requested: "
              + visitorsCount);
    }

    Booking booking = new Booking(bookingCode, user, visitorsNames);

    var updatedBookings = new ArrayList<>(visit.getBookings());

    updatedBookings.add(booking);
    visit.setBookings(updatedBookings);

    updateVisit(visit);
  }


  @Override
  /*@ also
    @ ensures \result.size() >= 0;
    @ ensures (\forall Booking b; \result.contains(b); 
    @          (\exists int visitId; visitId == visit.getId(); 
    @           bookingRepository.findAllByVisitId(visitId).contains(b)));
    @*/
  public List<Booking> getBookingsByVisit(Visit visit) {

    visitRepository.findById(visit.getId()).orElseThrow(() -> new VisitDayException(VisitDayErrorCode.VISIT_NOT_FOUND,
        "Visit with id " + visit.getId() + " not found"));

    return bookingRepository.findAllByVisitId(visit.getId());
  }


  @Override
  /*@ also
    @ ensures \result.size() >= 0;
    @ ensures (\forall Booking b; \result.contains(b); b.getUser().getId().equals(user.getId()));
    @*/
  public List<Booking> getBookingsByUser(User user) {
    userInfoService.findById(user.getId()).orElseThrow(
        () -> new UserException(UserErrorCode.USER_NOT_FOUND, "User with id " + user.getId() + " not found"));
    return bookingRepository.findAllByUserId(user.getId());
  }


  @Override
  /*@ also
    @ ensures !(\exists Booking b; getBookingsByUser(getBookingByCode(bookingCode).getUser()).contains(b);
    @           b.getBookingCode().equals(bookingCode));
    @ ensures getVisitByBookingCode(bookingCode).getAvailableSeats() > 
    @         \old(getVisitByBookingCode(bookingCode).getAvailableSeats());
    @*/
  public void cancelBooking(String bookingCode, int userId) {
    Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(
        () -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND, "No booking found with code: " + bookingCode));

    if (booking.getUser().getId() != userId) {
      throw new BookingException(BookingErrorCode.USER_NOT_AUTHORIZED,
          "User with id " + userId + " is not authorized to cancel this booking");
    }

    if (!isThisBookingCancellable(bookingCode)) {
      throw new BookingException(BookingErrorCode.BOOKING_NOT_CANCELLABLE, "Booking with code " + bookingCode
          + " is not cancellable (less than 3 days to the visit or visit status does not allow cancellations)");
    }

    Visit visit = visitRepository.findByBookingCode(bookingCode)
        .orElseThrow(() -> new VisitDayException(VisitDayErrorCode.VISIT_NOT_FOUND,
            "No visit found for booking code: " + bookingCode));

    List<Booking> updatedBookings =
        visit.getBookings().stream().filter(b -> !b.getBookingCode().equals(bookingCode)).toList();

    visit.setBookings(updatedBookings);

    updateVisit(visit);
  }


  @Override
  /*@ also
    @ ensures \result == (getVisitByBookingCode(bookingCode).getVisitStatus() == VisitStatus.PROPOSED ||
    @                    getVisitByBookingCode(bookingCode).getVisitStatus() == VisitStatus.FULL);
    @*/
  public boolean isThisBookingCancellable(String bookingCode) {
    Visit visit = visitRepository.findByBookingCode(bookingCode).orElseThrow(
        () -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND, "No booking found with code: " + bookingCode));

    // LocalDate today = LocalDate.now(clock);  // Rimossa variabile inutilizzata

    return (visit.getVisitStatus() == VisitStatus.PROPOSED || visit.getVisitStatus() == VisitStatus.FULL);
  }


  @Override
  /*@ also
    @ ensures \result.getBookingCode().equals(bookingCode);
    @ ensures bookingRepository.findByBookingCode(bookingCode).isPresent();
    @*/
  public Booking getBookingByCode(String bookingCode) {
    return bookingRepository.findByBookingCode(bookingCode)
        .orElseThrow(() -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND,
            "Booking with code " + bookingCode + " not found"));

  }


  private void updateVisit(Visit visit) {
    LocalDate visitDate = visit.getDate();
    VisitStatus status = visit.getVisitStatus();
    var visitType = visit.getVisitType();

    if (visitDate == null || status == null || visitType == null)
      throw new IllegalArgumentException("Visit data incomplete");

    VisitSchedulerService.setStatusToFullIfProposedVisitFull(visit);
    VisitSchedulerService.setStatusToProposedIfFullVisitNoLongerFull(visit);

    visitRepository.save(visit);
  }


  @Override
  /*@ also
    @ ensures \result != null;
    @ ensures getBookingsByVisit(\result).stream().anyMatch(b -> b.getBookingCode().equals(bookingCode));
    @*/
  public Visit getVisitByBookingCode(String bookingCode) {

    return visitRepository.findByBookingCode(bookingCode)
        .orElseThrow(() -> new VisitDayException(VisitDayErrorCode.VISIT_NOT_FOUND, "Visit not found"));
  }

}
