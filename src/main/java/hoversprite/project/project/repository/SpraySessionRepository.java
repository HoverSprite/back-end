package hoversprite.project.project.repository;

import hoversprite.project.project.model.entity.SpraySession;
import hoversprite.project.project.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpraySessionRepository extends JpaRepository<SpraySession, Long> {

    // Custom query method to find available spray sessions
    List<SpraySession> findByIsAvailableTrue();
    int countByTimeSlot_Id(Long timeSlotId);
    List<SpraySession> findByTimeSlot(TimeSlot timeSlot);
    @Query("SELECT COUNT(s) FROM SpraySession s WHERE s.timeSlot.id = :timeSlotId AND s.isAvailable = true")
    int countAvailableSessions(@Param("timeSlotId") Long timeSlotId);

    @Query("SELECT COUNT(s) FROM SpraySession s WHERE s.timeSlot.id = :timeSlotId AND s.date = :date")
    int countByTimeSlotIdAndDate(@Param("timeSlotId") Long timeSlotId, @Param("date") LocalDate date);
    List<SpraySession> findByTimeSlotAndDate(TimeSlot timeSlot, LocalDate date);

    int countByTimeSlotAndDate(TimeSlot timeSlot, LocalDate date);
}
