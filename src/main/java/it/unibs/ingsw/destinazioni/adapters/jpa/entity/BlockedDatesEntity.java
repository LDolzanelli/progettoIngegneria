package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "blocked_dates")
@Getter
@Setter
public class BlockedDatesEntity {

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
}
