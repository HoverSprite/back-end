package com.example.project.service;

import com.example.project.model.entity.TimeSlot;
import java.util.List;
import java.util.Optional;

public interface TimeSlotService {
    List<TimeSlot> findAllTimeSlots();
    Optional<TimeSlot> findTimeSlotById(Long id);
    TimeSlot createTimeSlot(TimeSlot timeSlot);
    TimeSlot updateTimeSlot(Long id, TimeSlot updatedTimeSlot);
    void deleteTimeSlot(Long id);
    List<TimeSlot> findAvailableTimeSlots();
}
