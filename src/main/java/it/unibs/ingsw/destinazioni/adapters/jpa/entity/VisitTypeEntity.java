package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "visit_types", schema = "destinazioni")
public class VisitTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Size(max = 100)
    @NotNull
    @Column(name = "meeting_point", nullable = false, length = 100)
    private String meetingPoint;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @NotNull
    @Column(name = "min_num_participants", nullable = false)
    private Integer minNumParticipants;

    @NotNull
    @Column(name = "max_num_participants", nullable = false)
    private Integer maxNumParticipants;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;


    @OneToMany(mappedBy = "visitTypeEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VisitDayEntity> visitDayEntities = new LinkedHashSet<>();


    @OneToMany(mappedBy = "visitType")
    private Set<VisitEntity> visitEntities = new LinkedHashSet<>();

}
