package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.MonthCollectionStateId;

@Repository
public interface MonthCollectionStateRepository extends JpaRepository<MonthCollectionStateEntity, MonthCollectionStateId> {

    boolean existsByMonthAndYear(int month, int year);
    
    MonthCollectionStateEntity findByMonthAndYear(int month, int year);
}
