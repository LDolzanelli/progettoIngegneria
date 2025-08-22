package it.unibs.ingsw.destinazioni.adapters.jpa.repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BookingId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, BookingId> {

    List<BookingEntity> findByIdBookingCode(String bookingCode);


    List<BookingEntity> findByVisit_Id(int visitId);


    List<BookingEntity> findByUser_Id(int userId);

}
