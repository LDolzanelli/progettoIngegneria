package it.unibs.ingsw.destinazioni.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitType {

    private Integer id;
    private String title;
    private String description;
    private String meetingPoint;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private int duration;
    private int maxParticipants;
    private int minParticipants;
    private boolean isFree;
    private List<DaysOfWeek> daysAvailable;
    private List<User> volunteers;
    

    public VisitType(Integer id, String title, String description, String meetingPoint, LocalDate startDate,
            LocalDate endDate, LocalTime startTime, int duration, int maxParticipants, int minParticipants,
            boolean isFree, List<DaysOfWeek> daysAvailable, List<User> volunteers2) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.meetingPoint = meetingPoint;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.duration = duration;
        this.maxParticipants = maxParticipants;
        this.minParticipants = minParticipants;
        this.isFree = isFree;
        this.daysAvailable = daysAvailable;
        this.volunteers = volunteers2;
    }


    /**
     * Costruttore per la creazione di un nuovo VisitType, la generazione dell'ID è gestita dal
     * database.
     */
    public VisitType(String title, String description, String meetingPoint, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, int duration, int maxParticipants, int minParticipants, boolean isFree, List<DaysOfWeek> daysAvailable, List<User> volunteers) {
        this(null, title, description, meetingPoint, startDate, endDate, startTime, duration, maxParticipants,
                minParticipants, isFree, daysAvailable, volunteers);
    }

}
