package com.example.project.service.impl;

import com.example.project.model.entity.SpraySession;
import com.example.project.model.entity.TimeSlot;
import com.example.project.repository.SpraySessionRepository;
import com.example.project.repository.TimeSlotRepository;
import com.example.project.service.SpraySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SpraySessionServiceImpl implements SpraySessionService {

    private final SpraySessionRepository spraySessionRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;


    @Autowired
    public SpraySessionServiceImpl(SpraySessionRepository spraySessionRepository) {
        this.spraySessionRepository = spraySessionRepository;
    }

    @Override
    public List<SpraySession> findAllSpraySessions() {
        return spraySessionRepository.findAll();
    }

    @Override
    public Optional<SpraySession> findSpraySessionById(Long id) {
        return spraySessionRepository.findById(id);
    }

    @Override
    public List<SpraySession> findAvailableSpraySessions() {
        return spraySessionRepository.findByIsAvailableTrue();
    }


    @Override
    public SpraySession createSpraySession(Long timeSlotId) {
        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(timeSlotId);

        if (timeSlot.isPresent()) {
            SpraySession spraySession = new SpraySession();
            spraySession.setTimeSlot(timeSlot.get());
            spraySession.setIsAvailable(true);

            return spraySessionRepository.save(spraySession);
        } else {
            throw new RuntimeException("TimeSlot not found with id: " + timeSlotId);
        }
    }

    @Override
    public SpraySession updateSpraySession(Long id, SpraySession updatedSpraySession) {
        Optional<SpraySession> existingSpraySession = spraySessionRepository.findById(id);
        if (existingSpraySession.isPresent()) {
            SpraySession spraySession = existingSpraySession.get();
            spraySession.setTimeSlot(updatedSpraySession.getTimeSlot());
            spraySession.setIsAvailable(updatedSpraySession.getIsAvailable());
            return spraySessionRepository.save(spraySession);
        } else {
            throw new RuntimeException("SpraySession not found with id: " + id);
        }
    }

    @Override
    public void deleteSpraySession(Long id) {
        spraySessionRepository.deleteById(id);
    }

    @Override
    public boolean checkAvailability(Long timeSlotId, LocalDate date) {
        // Check the number of existing sessions for the given time slot and date.
        int bookedSessions = spraySessionRepository.countByTimeSlotIdAndDate(timeSlotId, date);

        // If the number of booked sessions is less than 2, the time slot is available.
        return bookedSessions < 2;
    }


}
