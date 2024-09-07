package hoversprite.project.project.repository;

import hoversprite.project.project.model.entity.SpraySession_2;
import hoversprite.project.project.repository.custom.SpraySession2RepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SpraySession2Repository extends JpaRepository<SpraySession_2, Integer>, SpraySession2RepositoryCustom {
//    // Custom query method to find available spray sessions
//    List<SpraySession_2> findByIsAvailableTrue();
//    int countByTimeSlot_Id(Long timeSlotId);
//    List<SpraySession_2> findByTimeSlot(TimeSlot timeSlot);
//    @Query("SELECT COUNT(s) FROM SpraySession s WHERE s.timeSlot.id = :timeSlotId AND s.isAvailable = true")
//    int countAvailableSessions(@Param("timeSlotId") Long timeSlotId);
//
//    @Query("SELECT COUNT(s) FROM SpraySession s WHERE s.timeSlot.id = :timeSlotId AND s.date = :date")
//    int countByTimeSlotIdAndDate(@Param("timeSlotId") Long timeSlotId, @Param("date") LocalDate date);
//    List<SpraySession_2> findByTimeSlotAndDate(TimeSlot timeSlot, LocalDate date);

//    int countByTimeSlotAndDate(TimeSlot timeSlot, LocalDate date);
    List<SpraySession_2> findByDateAndStartTime(LocalDate date, LocalTime startTime);}
