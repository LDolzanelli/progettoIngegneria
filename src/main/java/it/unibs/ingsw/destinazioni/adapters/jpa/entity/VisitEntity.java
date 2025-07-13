package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "visits", schema = "destinazioni")
public class VisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "volunteer_id", referencedColumnName = "id", nullable = true)
    private UserEntity volunteer;

    @Size(max = 50)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visit_type_id", nullable = false)
    private VisitTypeEntity visitType;

    @ManyToMany
    private Set<UserEntity> users = new LinkedHashSet<>();

}