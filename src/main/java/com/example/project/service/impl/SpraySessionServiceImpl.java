package com.example.project.service.impl;

import com.example.project.model.entity.SpraySession;
import com.example.project.model.entity.TimeSlot;
import com.example.project.model.entity.TimeSlotDto;
import com.example.project.model.entity.WeekDayDto;
import com.example.project.repository.SpraySessionRepository;
import com.example.project.repository.TimeSlotRepository;
import com.example.project.service.SpraySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    public SpraySession createSpraySession(Long timeSlotId, Boolean isAvailable, LocalDate date) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found with id: " + timeSlotId));

        SpraySession spraySession = new SpraySession();
        spraySession.setTimeSlot(timeSlot);
        spraySession.setIsAvailable(isAvailable);
        spraySession.setDate(date);

        return spraySessionRepository.save(spraySession);
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


    @Override
    public List<WeekDayDto> getWeeklySchedule() {
        List<WeekDayDto> weekSchedule = new ArrayList<>();

        // Loop through each day of the week (starting from today)
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            WeekDayDto dayDto = new WeekDayDto();
            dayDto.setDayName(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            dayDto.setGregorianDate(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dayDto.setLunarDate(convertToLunarDate(date));  // Assuming this method exists

            List<TimeSlotDto> timeSlots = getTimeSlotsForDate(date);
            dayDto.setTimeSlots(timeSlots);

            weekSchedule.add(dayDto);
        }

        return weekSchedule;
    }

    private List<TimeSlotDto> getTimeSlotsForDate(LocalDate date) {
        List<TimeSlotDto> timeSlots = new ArrayList<>();

        // Fetch all the time slots
        List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();

        for (TimeSlot timeSlot : allTimeSlots) {
            int bookedSessions = spraySessionRepository.countByTimeSlotAndDate(timeSlot, date);

            TimeSlotDto timeSlotDto = new TimeSlotDto();
            timeSlotDto.setId(timeSlot.getId());
            timeSlotDto.setStartTime(timeSlot.getStartTime().toString());
            timeSlotDto.setEndTime(timeSlot.getEndTime().toString());
            timeSlotDto.setAvailable(bookedSessions < 2);  // Max 2 sessions per time slot

            timeSlots.add(timeSlotDto);
        }

        return timeSlots;
    }

    private String convertToLunarDate(LocalDate date) {
        // Implement this method based on your lunar calendar conversion logic
        return "Lunar Date";  // Placeholder
    }

}
