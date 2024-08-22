package com.example.project.controller;

import com.example.project.model.entity.TimeSlot;
import com.example.project.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/timeslots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @Autowired
    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    // Get all time slots
    @GetMapping
    public ResponseEntity<List<TimeSlot>> getAllTimeSlots() {
        List<TimeSlot> timeSlots = timeSlotService.findAllTimeSlots();
        return new ResponseEntity<>(timeSlots, HttpStatus.OK);
    }

    // Get a time slot by ID
    @GetMapping("/{id}")
    public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable Long id) {
        Optional<TimeSlot> timeSlot = timeSlotService.findTimeSlotById(id);
        return timeSlot.map(slot -> new ResponseEntity<>(slot, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Create a new time slot
    @PostMapping
    public ResponseEntity<TimeSlot> createTimeSlot(@RequestBody TimeSlot timeSlot) {
        TimeSlot newTimeSlot = timeSlotService.createTimeSlot(timeSlot);
        return new ResponseEntity<>(newTimeSlot, HttpStatus.CREATED);
    }

    // Update an existing time slot
    @PutMapping("/{id}")
    public ResponseEntity<TimeSlot> updateTimeSlot(@PathVariable Long id, @RequestBody TimeSlot updatedTimeSlot) {
        try {
            TimeSlot timeSlot = timeSlotService.updateTimeSlot(id, updatedTimeSlot);
            return new ResponseEntity<>(timeSlot, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a time slot
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        try {
            timeSlotService.deleteTimeSlot(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots() {
        List<TimeSlot> availableSlots = timeSlotService.findAvailableTimeSlots();
        return new ResponseEntity<>(availableSlots, HttpStatus.OK);
    }

}
