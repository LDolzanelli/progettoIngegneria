package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BlockedDatesEntity;

public interface BlockedDatesRepository extends JpaRepository<BlockedDatesEntity, LocalDate> {

    BlockedDatesEntity findByDate(LocalDate date);

    void deleteByDate(LocalDate date);

    boolean existsByDate(LocalDate date);


}
