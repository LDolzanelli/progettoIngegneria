package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "month_collection_state", schema = "destinazioni")
@IdClass(MonthCollectionStateId.class)
public class MonthCollectionStateEntity {

    @Id
    @NotNull
    @Column(name = "month", nullable = false)
    private int month;

    @Id
    @NotNull
    @Column(name = "year", nullable = false)
    private int year;

    @NotNull
    @Column(name = "volunteers_availability_collection_enabled", nullable = false)
    private boolean volunteersAvailabilityCollectionEnabled;

    @NotNull
    @Column(name = "visit_plan_created", nullable = false)
    private boolean visitPlanCreated;
}
