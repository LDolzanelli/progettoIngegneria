package it.unibs.ingsw.destinazioni.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import it.unibs.ingsw.destinazioni.domain.model.enums.VisitStatus;

@Getter
@Setter
public class Visit {
    private Integer id;
    private LocalDate date;
    private User volunteer;
    private String status;
    private VisitType visitType;
    private Set<User> participants;
    private VisitStatus visitStatus;


    public Visit(Integer id, LocalDate date, User volunteer, String status, VisitType visitType, Set<User> participants, VisitStatus visitStatus) {
        this.id = id;
        this.date = date;
        this.volunteer = volunteer;
        this.status = status;
        this.visitType = visitType;
        this.participants = participants;
        this.visitStatus = visitStatus;
    }

    public Visit(LocalDate date, User volunteer, String status, VisitType visitType, Set<User> participants, VisitStatus visitStatus) {
        this(null, date, volunteer, status, visitType, participants, visitStatus);
    }
}