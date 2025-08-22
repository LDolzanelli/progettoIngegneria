package it.unibs.ingsw.destinazioni.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;

@Setter
@Getter
public class Visit {
    private Integer id;
    private LocalDate date;
    private User volunteer;
    
    private VisitType visitType;
    private List<Booking> bookings;
    private VisitStatus visitStatus;


    public Visit(Integer id, LocalDate date, User volunteer, VisitType visitType, List<Booking> bookings ,VisitStatus visitStatus) {
        this.id = id;
        this.date = date;
        this.volunteer = volunteer;
        this.visitType = visitType;
        this.visitStatus = visitStatus;
        this.bookings = bookings;
    }

    public Visit(LocalDate date, User volunteer, VisitType visitType, List<Booking> bookings, VisitStatus visitStatus) {
        this(null, date, volunteer, visitType, bookings, visitStatus);
    }

    public int visitorsNumber() {
        return bookings.stream().mapToInt(Booking::getNumberOfVisitors).sum();
    }

    public int getAvailableSeats() {
        return visitType.getMaxParticipants() - visitorsNumber();
    }
}