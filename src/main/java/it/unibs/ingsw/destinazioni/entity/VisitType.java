package it.unibs.ingsw.destinazioni.entity;

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
public class VisitType {
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
    @Column(name = "meetingPoint", nullable = false, length = 100)
    private String meetingPoint;

    @NotNull
    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "startTime", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(name = "isFree", nullable = false)
    private Boolean isFree = false;

    @NotNull
    @Column(name = "minNumParticp", nullable = false)
    private Integer minNumParticp;

    @NotNull
    @Column(name = "maxNumPartec", nullable = false)
    private Integer maxNumPartec;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "visitType")
    private Set<VisitDay> visitDays = new LinkedHashSet<>();

    @OneToMany(mappedBy = "visitType")
    private Set<it.unibs.ingsw.destinazioni.entity.Visit> visits = new LinkedHashSet<>();

}