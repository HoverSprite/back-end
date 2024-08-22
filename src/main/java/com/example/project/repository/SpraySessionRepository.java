package com.example.project.repository;

import com.example.project.model.entity.SpraySession;
import com.example.project.model.entity.TimeSlot;
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

}
