package com.example.project.controller;

import com.example.project.model.entity.SpraySession;
import com.example.project.service.SpraySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/spraysessions")
public class SpraySessionController {

    private final SpraySessionService spraySessionService;

    @Autowired
    public SpraySessionController(SpraySessionService spraySessionService) {
        this.spraySessionService = spraySessionService;
    }

    // Get all spray sessions
    @GetMapping
    public ResponseEntity<List<SpraySession>> getAllSpraySessions() {
        List<SpraySession> spraySessions = spraySessionService.findAllSpraySessions();
        return new ResponseEntity<>(spraySessions, HttpStatus.OK);
    }

    // Get a specific spray session by ID
    @GetMapping("/{id}")
    public ResponseEntity<SpraySession> getSpraySessionById(@PathVariable Long id) {
        Optional<SpraySession> spraySession = spraySessionService.findSpraySessionById(id);
        return spraySession.map(session -> new ResponseEntity<>(session, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get all available spray sessions
    @GetMapping("/available")
    public ResponseEntity<List<SpraySession>> getAvailableSpraySessions() {
        List<SpraySession> availableSpraySessions = spraySessionService.findAvailableSpraySessions();
        return new ResponseEntity<>(availableSpraySessions, HttpStatus.OK);
    }

    // Create a new spray session
    @PostMapping
    public ResponseEntity<SpraySession> createSpraySession(@RequestParam Long timeSlotId) {
        SpraySession newSpraySession = spraySessionService.createSpraySession(timeSlotId);
        return new ResponseEntity<>(newSpraySession, HttpStatus.CREATED);
    }


    // Update an existing spray session
    @PutMapping("/{id}")
    public ResponseEntity<SpraySession> updateSpraySession(@PathVariable Long id, @RequestBody SpraySession updatedSpraySession) {
        try {
            SpraySession spraySession = spraySessionService.updateSpraySession(id, updatedSpraySession);
            return new ResponseEntity<>(spraySession, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a spray session
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpraySession(@PathVariable Long id) {
        try {
            spraySessionService.deleteSpraySession(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam("timeSlotId") Long timeSlotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isAvailable = spraySessionService.checkAvailability(timeSlotId, date);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }


}
